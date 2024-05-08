package com.rfdotech.auth.data.network

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTimestamp: Long,
    val userId: String
)
