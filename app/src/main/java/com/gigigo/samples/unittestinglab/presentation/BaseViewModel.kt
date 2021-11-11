package com.gigigo.samples.unittestinglab.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

typealias Reducer<T> = T.() -> T

abstract class BaseViewModel<V, I>(
    private val dispatchers: CoroutinesDispatchers
) : ViewModel() {

    protected abstract val initialViewState: V

    private val _viewState: MutableLiveData<V> by lazy { MutableLiveData(initialViewState) }

    val viewState: LiveData<V> by lazy { _viewState }

    fun getState(): V = viewState.value ?: throw IllegalStateException()

    protected fun setState(reduce: Reducer<V>) {
        _viewState.value = reduce(getState())
    }

    abstract fun sendIntent(intent: I)

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(context = dispatchers.Main, block = block)
    }
}
