package com.example.nahachilzanoch.data.remote.models

import com.google.gson.annotations.SerializedName

data class TaskListRequest(
    @SerializedName("list") val list: List<TaskNWModel>
)