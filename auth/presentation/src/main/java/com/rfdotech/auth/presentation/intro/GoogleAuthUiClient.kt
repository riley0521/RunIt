package com.rfdotech.auth.presentation.intro

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.printAndThrowCancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val oneTapClient: SignInClient,
    private val onSignOut: () -> Unit
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

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            onSignOut()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId("Something")
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }
}