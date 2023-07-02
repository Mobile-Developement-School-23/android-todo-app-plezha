package com.example.nahachilzanoch.data.remote.models

import com.google.gson.annotations.SerializedName

data class TaskRequest(
    @SerializedName("element") val task: TaskNWModel
)