package com.rfdotech.core.data.networking

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expirationTimestamp: Long
)
