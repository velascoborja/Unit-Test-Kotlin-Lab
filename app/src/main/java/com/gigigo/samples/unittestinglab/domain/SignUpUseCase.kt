package com.gigigo.samples.unittestinglab.domain

import kotlinx.coroutines.delay

interface SignUpUseCase {

    suspend operator fun invoke(data: SignUpData): Boolean
}

class SignUp : SignUpUseCase {

    override suspend fun invoke(data: SignUpData): Boolean {
        delay(2000)
        return true
    }
}
