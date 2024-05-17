package com.rfdotech.core.domain.auth

typealias UserId = String

interface UserStorage {

    suspend fun set(userId: UserId?)

    suspend fun get(): UserId?
}