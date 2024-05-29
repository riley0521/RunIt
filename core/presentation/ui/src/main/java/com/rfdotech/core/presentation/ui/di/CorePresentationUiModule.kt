package com.rfdotech.core.presentation.ui.di

import com.google.android.gms.auth.api.identity.Identity
import com.rfdotech.core.presentation.ui.auth.GoogleAuthUiClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val corePresentationUiModule = module {
    single {
        val signInClient = Identity.getSignInClient(androidApplication())
        GoogleAuthUiClient(signInClient)
    }
}