package com.solkismet.urbandictionary.ui.extensions

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.MutableLiveData
import java.util.*

// This is a helper class with a flag to indicate whether it's running or not
// so that it will be started again unless it's finished.
abstract class RunnableWithFlag : Runnable {
    var running: Boolean = false
}

// Mutex that is lazily initialized
val <T> MutableLiveData<T>.dataLock: Any by lazy {
    Any()
}

// Queue for posting values that is lazily initialized
val <T> MutableLiveData<T>.queue: Queue<T> by lazy {
    ArrayDeque<T>()
}

// The runnable that will be used to post values added to the queue
val <T> MutableLiveData<T>.postRunnable: RunnableWithFlag
    get() {

        return object: RunnableWithFlag() {
            override fun run() {
                running = true
                while (!queue.isEmpty()) {
                    val newValue: T
                    synchronized(dataLock) {
                        newValue = queue.remove()
                    }
                    value = newValue
                }
                running = false
            }
        }
    }

// Sometimes posting values to LiveData may cause previously set, but not yet observed, values
// to be overwritten.  This extension method leverages a queue that will accept new values
// without overwriting old values.
fun <T> MutableLiveData<T>.postToQueue(value: T) {
    synchronized(dataLock) {
        queue.add(value)
    }
    if (postRunnable.running) {
        return
    }
    ArchTaskExecutor.getInstance().postToMainThread(postRunnable)
}
