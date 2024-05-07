package com.rfdotech.auth.data.repository

import com.rfdotech.auth.data.network.SignUpRequest
import com.rfdotech.auth.domain.AuthRepository
import com.rfdotech.core.data.networking.post
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<SignUpRequest, Unit>(
            route = "/register",
            body = SignUpRequest(
                email = email,
                password = password
            )
        )
    }
}