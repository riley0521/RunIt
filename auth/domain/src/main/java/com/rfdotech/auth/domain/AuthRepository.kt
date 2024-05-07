package com.rfdotech.auth.domain

import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult

interface AuthRepository {

    suspend fun signUp(email: String, password: String): EmptyResult<DataError.Network>
}