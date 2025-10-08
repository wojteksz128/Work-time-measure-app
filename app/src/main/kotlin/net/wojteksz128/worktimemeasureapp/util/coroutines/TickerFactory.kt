package net.wojteksz128.worktimemeasureapp.util.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class TickerFactory @Inject constructor() {

    fun create(scope: CoroutineScope): SharedFlow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }.shareIn(scope, SharingStarted.WhileSubscribed(5000))
}