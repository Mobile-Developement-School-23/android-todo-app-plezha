package com.example.nahachilzanoch.data.remote.models

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("status") val status: String,
    @SerializedName("element") val task: TaskNWModel,
    @SerializedName("revision") val revision: Int,
)