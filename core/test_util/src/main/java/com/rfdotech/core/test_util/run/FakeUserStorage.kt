package com.rfdotech.core.test_util.run

import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.auth.UserStorage

class FakeUserStorage : UserStorage {

    var userId: String? = null
        private set

    override suspend fun set(userId: UserId?) {
        this.userId = userId
    }

    override suspend fun get(): UserId? {
        return userId
    }
}