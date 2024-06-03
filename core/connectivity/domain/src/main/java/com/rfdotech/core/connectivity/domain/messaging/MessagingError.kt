package com.rfdotech.core.connectivity.domain.messaging

import com.rfdotech.core.domain.util.Error

enum class MessagingError: Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}