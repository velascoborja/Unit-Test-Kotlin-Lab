package com.gigigo.samples.unittestinglab

import androidx.lifecycle.Observer

class TestObserver<T> : Observer<T> {

    private val _values: MutableList<T> = mutableListOf()

    val values: List<T> get() = _values

    override fun onChanged(t: T) {
        _values.add(t)
    }
}
