package com.rfdotech.auth.data.repository

import com.rfdotech.auth.data.network.SignInRequest
import com.rfdotech.auth.data.network.SignInResponse
import com.rfdotech.auth.data.network.SignUpRequest
import com.rfdotech.auth.domain.AuthRepository
import com.rfdotech.core.data.networking.post
import com.rfdotech.core.domain.AuthInfo
import com.rfdotech.core.domain.SessionStorage
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import com.rfdotech.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val sessionStorage: SessionStorage,
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

    override suspend fun signIn(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<SignInRequest, SignInResponse>(
            route = "/login",
            body = SignInRequest(
                email = email,
                password = password
            )
        )

        if (result is Result.Success) {
            with (result.data) {
                val authInfo = AuthInfo(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId
                )

                sessionStorage.set(authInfo)
            }
        }

        return result.asEmptyDataResult()
    }
}