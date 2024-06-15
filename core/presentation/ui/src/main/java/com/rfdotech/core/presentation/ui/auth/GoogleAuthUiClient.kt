package com.rfdotech.core.presentation.ui.auth

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.printAndThrowCancellationException
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.presentation.ui.BuildConfig
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val oneTapClient: SignInClient
) {

    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            null
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): UserId? {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            user?.uid
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            null
        }
    }

    suspend fun signOut(): EmptyResult<DataError.Network> {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    suspend fun deleteAccount(): Result<Boolean, DataError.Network> {
        return try {
            auth.currentUser?.delete()?.await()
            Result.Success(true)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            e.printAndThrowCancellationException()

            signOut()
            Result.Error(DataError.Network.RE_AUTHENTICATE)
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.AUTH_API_KEY)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }
}