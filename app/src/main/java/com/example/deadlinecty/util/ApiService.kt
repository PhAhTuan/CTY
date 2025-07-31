package com.example.deadlinecty.util


import com.example.deadlinecty.cty2.GenerateResponse
import com.example.deadlinecty.cty2.MediaResponse
import com.example.deadlinecty.model.ConfigResponse
import com.example.deadlinecty.model.LoginBody
import com.example.deadlinecty.model.SessionModel
import com.example.deadlinecty2.data.GroupChatApiResponse
import com.example.deadlinecty2.data.MessageApiResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("api/v10/sessions")
    fun getSession(
        @Header("ProjectId") projectId: Int,
    ): Call<SessionModel>

    @GET("api/v10/configs")
    fun getConfig(
        @Header("ProjectId") projectId: Int,
        @Query("restaurant_name") restaurantName: String,
        @Query("project_id") projectIdParam: String,
    ): Call<ConfigResponse>

    @POST("api/v10/employees/login")
    fun login(
        @Header("Authorization") authorization: String,
        @Header("ProjectId") projectId: Int,
        @Body body: LoginBody
    ): Call<com.example.deadlinecty.model.LoginResponse>
}
//---------------------------------------
interface ChatApiService {
    @GET("api/v1/conversation/list-conversation")
    suspend fun getGroupChats(
        @Query("object_type") objectType: Int = -1,
        @Query("limit") limit: Int = 10,
        @Query("type") type: Int = 1,
        @Header("ProjectId") projectId: Int = 9024,
        @Header("Authorization") authorization: String,
        @Header("Method") method: Int = 0
    ): GroupChatApiResponse

    @GET("api/v1/message/list-message")
    suspend fun getMessages(
        @Query("arrow") arrow: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("conversation_id") conversationId: String,
        @Header("ProjectId") projectId: Int = 9025,
        @Header("Authorization") authorization: String,
        @Header("Method") method: Int = 0
    ): MessageApiResponse
}
//----------------------------------------
interface MediaService{
    @POST("api/v2/media/generate")
    suspend fun upgenerateImage(
        @Header("Authorization") authorization: String,
        @Header("ProjectId") projectId: Int = 9007,
        @Header("Method")  method: Int = 1,
        @Body mediaResponse: MediaResponse
    ):  retrofit2.Response<GenerateResponse>

    @Multipart
    @POST("api/v2/media/upload")
    suspend fun uploadImage(
        @Header("Authorization") authorization: String,
        @Header("ProjectId") projectId: Int = 9007,
        @Header("Method")  method: Int = 1,
        @Part medias: List<MultipartBody.Part>
    ): retrofit2.Response<GenerateResponse>
}