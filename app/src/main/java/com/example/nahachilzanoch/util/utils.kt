package com.example.nahachilzanoch.util

import java.util.Date

fun Long.getDate(): String {
    return Date(this).toString()
}