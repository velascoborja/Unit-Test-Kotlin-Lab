@file:Suppress("FunctionName")

package com.gigigo.samples.unittestinglab.presentation

import java.util.regex.Pattern

val MailPattern: Pattern = Pattern.compile(
    """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+"""
)

interface Validator<T> {

    fun validate(value: T): Boolean

    companion object {
        operator fun <T> invoke(block: (T) -> Boolean) = object : Validator<T> {
            override fun validate(value: T): Boolean = block(value)
        }
    }
}

fun MinLengthTextValidator(minLength: Int) = Validator<String> { it.length >= minLength }

fun PatternValidator(pattern: Pattern) = Validator<String> { pattern.matcher(it).matches() }

fun EmailValidator() = PatternValidator(MailPattern)

fun NotEmptyTextValidator(): Validator<String> = MinLengthTextValidator(1)
