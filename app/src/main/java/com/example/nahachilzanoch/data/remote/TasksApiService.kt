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

private const val AUTH_TOKEN = "bespin"
private const val AUTH_HEADER =  "Authorization: Bearer $AUTH_TOKEN"
interface TasksApiService {
    @Headers(AUTH_HEADER)
    @GET("list")
    suspend fun getTasks(): Response<TaskListResponse>

    @Headers(AUTH_HEADER)
    @PATCH("list")
    suspend fun patchTasks(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TaskListRequest
    ): Response<TaskListResponse>

    @Headers(AUTH_HEADER)
    @GET("list/{taskId}")
    suspend fun getTask(@Path("taskId") id: String): Response<TaskResponse>

    @Headers(AUTH_HEADER)
    @POST("list")
    suspend fun postTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TaskRequest
    ): Response<TaskResponse>

    @Headers(AUTH_HEADER)
    @PUT("list/{taskId}")
    suspend fun putTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("taskId") id: String,
        @Body request: TaskRequest,
    ): Response<TaskResponse>

    @Headers(AUTH_HEADER)
    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): Response<TaskResponse>

}