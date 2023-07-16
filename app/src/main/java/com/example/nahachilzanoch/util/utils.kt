package com.example.nahachilzanoch.util

import com.example.nahachilzanoch.data.local.Urgency
import kotlinx.coroutines.delay
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException

fun Long.getDateString(): String {
    return Date(this)
        .toString()
        .split(" ")
        .slice(0..2)
        .joinToString(" ")
}

fun Long.getDateAndTimeString(): String = Date(this).toString()

fun String.toUrgency(): Urgency = when(this) {
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