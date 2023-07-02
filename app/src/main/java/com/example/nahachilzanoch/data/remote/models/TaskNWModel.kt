package com.example.nahachilzanoch.data.remote.models

import com.google.gson.annotations.SerializedName

data class TaskNWModel(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("importance") val importance: String,
    @SerializedName("done") val isDone: Boolean,
    @SerializedName("created_at") val createdAt: Int,
    @SerializedName("deadline") val deadline: Int? = null,
    @SerializedName("changed_at") val changedAt: Int,
    @SerializedName("color") val color: String? = null,
    @SerializedName("last_updated_by") val device: String,
)