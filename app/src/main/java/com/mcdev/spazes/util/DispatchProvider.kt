package com.mcdev.spazes.util

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}