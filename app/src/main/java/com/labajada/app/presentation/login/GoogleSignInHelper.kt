package com.labajada.app.presentation.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.labajada.app.R

object GoogleSignInHelper {

    suspend fun obtenerIdToken(context: Context): Result<String> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id ))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request)

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleIdTokenCredential.idToken)
            } else {
                Result.failure(Exception("Credencial de Google inválida."))
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInHelper", "Error obteniendo credencial de Google", e)
            Result.failure(Exception("No se pudo iniciar sesión con Google. Intenta de nuevo."))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("GoogleSignInHelper", "Error parseando el token de Google", e)
            Result.failure(Exception("Ocurrió un error con tu cuenta de Google."))
        }
    }
}