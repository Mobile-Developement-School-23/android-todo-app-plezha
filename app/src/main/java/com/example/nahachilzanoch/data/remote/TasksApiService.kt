package com.example.nahachilzanoch.data.remote

import com.example.nahachilzanoch.data.remote.models.TaskListRequest
import com.example.nahachilzanoch.data.remote.models.TaskListResponse
import com.example.nahachilzanoch.data.remote.models.TaskRequest
import com.example.nahachilzanoch.data.remote.models.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TasksApiService {
    @Headers("Authorization: Bearer bespin")
    @GET("list")
    suspend fun getTasks(): Response<TaskListResponse>

    @Headers("Authorization: Bearer bespin")
    @PATCH("list")
    suspend fun patchTasks(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TaskListRequest
    ): Response<TaskListResponse>

    @Headers("Authorization: Bearer bespin")
    @GET("list/{taskId}")
    suspend fun getTask(@Path("taskId") id: String): Response<TaskResponse>

    @Headers("Authorization: Bearer bespin")
    @POST("list")
    suspend fun postTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TaskRequest
    ): Response<TaskResponse>

    @Headers("Authorization: Bearer bespin")
    @PUT("list/{taskId}")
    suspend fun putTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("taskId") id: String,
        @Body request: TaskRequest,
    ): Response<TaskResponse>

    @Headers("Authorization: Bearer bespin")
    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<TaskResponse>

}