package com.rfdotech.core.domain

data class TextWithContentDesc(
    val text: String,
    val contentDesc: String = text
) {
    override fun toString(): String {
        return text
    }
}
