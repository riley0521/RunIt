package com.rfdotech.auth.data.di

import com.rfdotech.auth.data.EmailPatternValidator
import com.rfdotech.auth.data.repository.AuthRepositoryImpl
import com.rfdotech.auth.domain.AuthRepository
import com.rfdotech.auth.domain.PatternValidator
import com.rfdotech.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}