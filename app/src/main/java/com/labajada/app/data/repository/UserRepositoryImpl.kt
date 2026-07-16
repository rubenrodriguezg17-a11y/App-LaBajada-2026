package com.labajada.app.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.labajada.app.data.local.dao.UserDao
import com.labajada.app.data.local.entity.SessionEntity
import com.labajada.app.data.local.entity.UserEntity
import com.labajada.app.data.mapper.toDomain
import com.labajada.app.domain.model.GoogleLoginResult
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.model.User
import com.labajada.app.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private fun rolDe(isBuyer: Boolean, isOwner: Boolean): String = when {
        isBuyer && isOwner -> "DUAL"
        isOwner -> "RESTAURANT"
        isBuyer -> "BUYER"
        else -> ""
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult: AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se obtuvo el UID de Firebase."))

            val userEntity = userDao.getUserById(uid)
                ?: return Result.failure(Exception("Esta cuenta no está registrada en La Bajada."))

            saveSession(Session(userId = uid, email = email, role = rolDe(userEntity.isBuyer, userEntity.isOwner)))
            Result.success(userEntity.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en operacion de autenticacion", e)
            Result.failure(e)
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        isBuyer: Boolean,
        isOwner: Boolean
    ): Result<User> {
        return try {
            val authResult: AuthResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se obtuvo el UID de Firebase."))

            // El envío del correo de verificación se intenta aparte: si falla (p.ej. un hipo
            // de red), no debe tumbar el registro que ya se completó en Firebase Auth. El
            // usuario igual puede reenviarlo desde la pantalla de verificación.
            try {
                authResult.user?.sendEmailVerification()?.await()
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "No se pudo enviar el correo de verificación inicial", e)
            }

            val entity = UserEntity(
                uid = uid,
                email = email,
                fullName = fullName,
                phoneNumber = phoneNumber,
                isBuyer = isBuyer,
                isOwner = isOwner
            )
            userDao.insertUser(entity)
            saveSession(Session(userId = uid, email = email, role = rolDe(isBuyer, isOwner)))
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en operacion de autenticacion", e)
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<GoogleLoginResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult: AuthResult = firebaseAuth.signInWithCredential(credential).await()
            val fbUser = authResult.user
                ?: return Result.failure(Exception("No se pudo iniciar sesión con Google."))
            val uid = fbUser.uid

            var userEntity = userDao.getUserById(uid)
            val esUsuarioNuevo = userEntity == null

            if (userEntity == null) {
                userEntity = UserEntity(
                    uid = uid,
                    email = fbUser.email ?: "",
                    fullName = fbUser.displayName ?: "Usuario de Google",
                    phoneNumber = "",
                    isBuyer = true,
                    isOwner = false
                )
                userDao.insertUser(userEntity)
            }

            saveSession(Session(userId = uid, email = userEntity.email, role = rolDe(userEntity.isBuyer, userEntity.isOwner)))
            Result.success(GoogleLoginResult(user = userEntity.toDomain(), isNewUser = esUsuarioNuevo))
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en login con Google", e)
            Result.failure(e)
        }
    }

    override suspend fun isGoogleUser(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        return user.providerData.any{ it.providerId == GoogleAuthProvider.PROVIDER_ID }
    }

    override suspend fun getUserById(uid: String): User? {
        return userDao.getUserById(uid)?.toDomain()
    }

    override suspend fun activateBuyerRole(uid: String) {
        userDao.activateBuyerRole(uid)
    }

    override suspend fun activateOwnerRole(uid: String) {
        userDao.activateOwnerRole(uid)
    }

    override suspend fun updateProfile(uid: String, fullName: String, phoneNumber: String): Result<User> {
        return try {
            val nombreLimpio = fullName.trim()
            if (nombreLimpio.isBlank()) {
                return Result.failure(Exception("El nombre no puede estar vacío."))
            }
            userDao.updateProfile(uid, nombreLimpio, phoneNumber.trim())
            val actualizado = userDao.getUserById(uid)
                ?: return Result.failure(Exception("No se encontró el usuario a actualizar."))
            Result.success(actualizado.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error actualizando perfil", e)
            Result.failure(e)
        }
    }

    override suspend fun saveSession(session: Session) {
        userDao.logout()
        userDao.saveSession(
            SessionEntity(
                id = 1,
                userId = session.userId,
                email = session.email,
                role = session.role
            )
        )
    }

    override suspend fun getActiveSession(): Session? {
        return userDao.getActiveSession()?.toDomain()
    }

    override suspend fun logout() {
        userDao.logout()
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en operacion de autenticacion", e)
            Result.failure(e)
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay sesión activa."))
            val email = user.email
                ?: return Result.failure(Exception("No se pudo verificar el correo de la cuenta."))

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en operacion de autenticacion", e)
            Result.failure(e)
        }
    }

    override suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay sesión activa."))
            val currentEmail = user.email
                ?: return Result.failure(Exception("No se pudo verificar el correo de la cuenta."))

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(currentEmail, currentPassword)
            user.reauthenticate(credential).await()
            user.verifyBeforeUpdateEmail(newEmail).await()
            Result.success(Unit)
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            android.util.Log.e("UserRepository", "Contraseña actual incorrecta", e)
            Result.failure(Exception("La contraseña actual es incorrecta."))
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            android.util.Log.e("UserRepository", "Correo ya registrado en otra cuenta", e)
            Result.failure(Exception("Ese correo ya está registrado en otra cuenta."))
        } catch (e: com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
            android.util.Log.e("UserRepository", "Requiere reautenticación reciente", e)
            Result.failure(Exception("Por seguridad, cierra sesión, vuelve a entrar e intenta de nuevo."))
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error cambiando correo", e)
            Result.failure(Exception(e.message ?: "No se pudo cambiar el correo."))
        }
    }

    override suspend fun deactivateAccount(currentPassword: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay sesión activa."))
            val email = user.email
                ?: return Result.failure(Exception("No se pudo verificar el correo de la cuenta."))

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            userDao.deactivateUser(user.uid)
            userDao.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error en operacion de autenticacion", e)
            Result.failure(e)
        }
    }

    override suspend fun isCurrentUserEmailVerified(): Boolean {
        return try {
            val user = firebaseAuth.currentUser ?: return false
            // reload() es necesario: Firebase cachea el estado localmente y no se entera
            // de que el usuario tocó el enlace del correo hasta que se le pide explícitamente.
            user.reload().await()
            firebaseAuth.currentUser?.isEmailVerified ?: false
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error verificando estado de verificación de correo", e)
            false
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No hay sesión activa."))
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error reenviando correo de verificación", e)
            Result.failure(e)
        }
    }

    override suspend fun refreshEmailFromFirebase(): String? {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return null
            firebaseUser.reload().await()

            val emailActualizado = firebaseAuth.currentUser?.email ?: return null
            val uid = firebaseUser.uid
            val userLocal = userDao.getUserById(uid) ?: return null

            if (userLocal.email != emailActualizado) {
                userDao.updateEmail(uid, emailActualizado)

                val sesionActual = userDao.getActiveSession()
                if (sesionActual != null && sesionActual.userId == uid) {
                    userDao.saveSession(sesionActual.copy(email = emailActualizado))
                }
                emailActualizado
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Error sincronizando correo con Firebase", e)
            null
        }
    }}