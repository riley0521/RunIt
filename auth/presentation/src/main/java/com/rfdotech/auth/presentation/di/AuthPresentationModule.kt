package com.rfdotech.auth.presentation.di

import com.rfdotech.auth.presentation.registration.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegistrationViewModel)
}