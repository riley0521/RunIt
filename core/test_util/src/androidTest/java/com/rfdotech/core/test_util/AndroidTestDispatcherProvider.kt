package com.rfdotech.core.test_util

import com.rfdotech.core.domain.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class AndroidTestDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val io: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val default: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val unconfined: CoroutineDispatcher
        get() = StandardTestDispatcher()
}