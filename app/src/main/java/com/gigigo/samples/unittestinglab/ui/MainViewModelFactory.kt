package com.gigigo.samples.unittestinglab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gigigo.samples.unittestinglab.domain.SignUp
import com.gigigo.samples.unittestinglab.presentation.CoroutinesDispatchersImpl

class MainViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            signUp = SignUp(),
            dispatchers = CoroutinesDispatchersImpl
        ) as T
    }
}
