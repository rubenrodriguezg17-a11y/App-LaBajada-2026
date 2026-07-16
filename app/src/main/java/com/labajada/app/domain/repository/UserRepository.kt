package com.labajada.app.domain.repository

import com.labajada.app.domain.model.GoogleLoginResult
import com.labajada.app.domain.model.Session
import com.labajada.app.domain.model.User

interface UserRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, password: String, fullName: String, phoneNumber: String, isBuyer: Boolean, isOwner: Boolean): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<GoogleLoginResult>
    suspend fun isGoogleUser(): Boolean

    suspend fun getUserById(uid: String): User?
    suspend fun activateBuyerRole(uid: String)
    suspend fun activateOwnerRole(uid: String)
    suspend fun updateProfile(uid: String, fullName: String, phoneNumber: String): Result<User>

    suspend fun saveSession(session: Session)
    suspend fun getActiveSession(): Session?
    suspend fun logout()

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit>
    suspend fun deactivateAccount(currentPassword: String): Result<Unit>

    suspend fun refreshEmailFromFirebase(): String?

    /**
     * Recarga el usuario de Firebase y devuelve si su correo ya fue verificado
     * (tocó el enlace que le enviamos). Para cuentas de Google esto es siempre true,
     * porque Firebase las marca como verificadas automáticamente al iniciar sesión.
     */
    suspend fun isCurrentUserEmailVerified(): Boolean

    /** Reenvía el correo de verificación al usuario que tiene la sesión activa. */
    suspend fun sendEmailVerification(): Result<Unit>
}