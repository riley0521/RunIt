package com.rfdotech.core.data.networking

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AccessTokenRequest(
    val refreshToken: String,
    val userId: String
)
