package com.rfdotech.core.domain

import kotlin.math.pow
import kotlin.math.round

const val ONE_DECIMAL = 1

fun Double.roundToDecimals(decimalCount: Int = ONE_DECIMAL): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}