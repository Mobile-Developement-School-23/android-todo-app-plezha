package com.example.nahachilzanoch.data.remote.models

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("list") val taskList: List<TaskNWModel>,
    @SerializedName("revision") val revision: Int,
)