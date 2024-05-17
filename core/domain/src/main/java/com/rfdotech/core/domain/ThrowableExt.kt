package com.rfdotech.core.domain

import kotlinx.coroutines.CancellationException

fun Throwable.printAndThrowCancellationException() {
    this.printStackTrace()
    if (this is CancellationException) throw this
}