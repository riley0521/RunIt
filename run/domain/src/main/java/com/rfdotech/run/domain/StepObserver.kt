package com.rfdotech.run.domain

import kotlinx.coroutines.flow.Flow

interface StepObserver {

    fun observeSteps(currentSteps: Int): Flow<Int>
}