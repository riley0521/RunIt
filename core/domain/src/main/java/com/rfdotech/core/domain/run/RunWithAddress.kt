package com.rfdotech.core.domain.run

import com.rfdotech.core.domain.Address

data class RunWithAddress(
    val run: Run,
    val address: Address?
)
