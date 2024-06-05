package com.rfdotech.auth.data.network

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SignUpRequest(
    val email: String,
    val password: String
)
