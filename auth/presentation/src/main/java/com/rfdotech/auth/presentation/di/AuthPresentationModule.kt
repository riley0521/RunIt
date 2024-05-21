package com.rfdotech.auth.presentation.di

import com.rfdotech.auth.presentation.intro.IntroViewModel
import com.rfdotech.auth.presentation.sign_in.SignInViewModel
import com.rfdotech.auth.presentation.sign_up.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::IntroViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::SignInViewModel)
}