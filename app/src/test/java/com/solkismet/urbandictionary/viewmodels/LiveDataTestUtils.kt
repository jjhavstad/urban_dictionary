package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/* Copyright 2019 Google LLC.
   SPDX-License-Identifier: Apache-2.0 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun <T> LiveData<T>.getOrAwaitValueWithCallback(
    onResultReadyCallback: MutableList<(value: T?) -> Unit>
) {
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            onResultReadyCallback.first()(o)
            onResultReadyCallback.removeAt(0)
            if (onResultReadyCallback.isEmpty()) {
                this@getOrAwaitValueWithCallback.removeObserver(this)
            }
        }
    }

    this.observeForever(observer)
}
