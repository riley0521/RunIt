package com.rfdotech.auth.domain

interface PatternValidator {

    fun matches(value: String): Boolean
}