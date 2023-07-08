package com.example.nahachilzanoch.util

import android.content.Context
import android.provider.Settings.Secure
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.local.Urgency
import com.example.nahachilzanoch.data.remote.models.TaskListRequest
import com.example.nahachilzanoch.data.remote.models.TaskListResponse
import com.example.nahachilzanoch.data.remote.models.TaskNWModel
import com.example.nahachilzanoch.data.remote.models.TaskRequest
import com.example.nahachilzanoch.data.remote.models.TaskResponse
import kotlinx.coroutines.delay
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException

fun Long.getDate(): String {
    return Date(this).toString()
}

fun String.toUrgency(): Urgency = when(this){
    Urgency.LOW.importance -> Urgency.LOW
    Urgency.NORMAL.importance -> Urgency.NORMAL
    Urgency.URGENT.importance -> Urgency.URGENT
    else -> throw IllegalStateException("Urgency can't be formed from \"$this\"")
}

suspend fun <T> withRetry(
    tryCnt: Int = 3,
    fallbackValue: T? = null,
    intervalMillis: (attempt: Int) -> Long = { it*1000L },
    retryCheck: (Throwable) -> Boolean = { true },
    block: suspend () -> T,
): T {
    try {
        val retryCnt = tryCnt - 1
        repeat(retryCnt) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (e is CancellationException || !retryCheck(e)) {
                    throw e
                }
            }
            delay(intervalMillis(attempt + 1))
        }
        return block()
    } catch (e: Exception) {
        if (e is CancellationException) {
            throw e
        }
        return fallbackValue ?: throw e
    }
}