package com.example.deadlinecty2.data


import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deadlinecty.cty2.MediaItem
import com.example.deadlinecty.cty2.MediaItemUpload
import com.example.deadlinecty.cty2.MediaResponse
import com.example.deadlinecty.util.RetrofitPhoto
import com.example.deadlinecty.util.RetrofitclientChat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.forEachIndexed

class HomeViewModel : ViewModel() {
    var groupList = mutableStateListOf<DataChat>()
        private set
    fun loadGroupChats() {
        viewModelScope.launch {
            try {
                val response = RetrofitclientChat.RetrofitClient.apiServicechat.getGroupChats(
                    authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0")
                Log.d("API_RESPONSE",  response.data.toString())
                if (response.status == 200) {
                    groupList.clear()
                    groupList.addAll(
                        response.data.map { item ->
                            Log.d("item", Gson().toJson(item))
                            DataChat(
                                conversationId = item.conversationId  ,
                                name = item.name ,
                                lastMessage = item.lastMessage.message.ifBlank { "Không có tin nhắn" },
                                time = item.lastActivity ,
                                avatarUrl = item.avatar.thumb.url,
                                noOfNotSeen = item.noOfNotSeen ,
                                noOfMember = item.noOfMember
                            )
                        }
                    )
                }
                Log.d("groupList", groupList.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("groupList err", e.toString())

            }
        }
    }
}

class MessageViewModel : ViewModel() {
    var tinNhanList = mutableStateListOf<TinNhan>()
        private set

    fun loadMessagesFromApi(context: Context, conversationId: String) {
        val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)
        val userIdCache = sharedPref.getInt("user_id", 0)
        val token = sharedPref.getString("access_token", "")

        viewModelScope.launch {
            try {
                val response = RetrofitclientChat.RetrofitClient.apiServicechat.getMessages(
                    conversationId = conversationId,
                    authorization = "Bearer $token"
                )
                Log.d("messageList", Gson().toJson(response))

                tinNhanList.clear()
                tinNhanList.addAll(
                    response.data.map { messageData ->
                        val firstMedia = messageData.media.firstOrNull()

                        TinNhan(
                            isMine = messageData.user.userId == userIdCache,
                            message = messageData.message,
                            createdAt = messageData.createdAt,
                            groupId = conversationId,
                            media = messageData.media,
                            messageType = messageData.messageType,
                            time = "",
                            audioUrl = null,
                            imageUrl = firstMedia?.original?.url,
                            keyError = randomKey(),
                        )
                    }
                )

                Log.d("tinnhanlist", Gson().toJson(tinNhanList))
            } catch (e: Exception) {
                Log.e("MessageViewModel", "Error loading messages: ${e.message}")
            }
        }
    }

    fun uploadMedia(mediaResponse: MediaResponse, uri: Uri,context: Context, conversationId: String) {
        viewModelScope.launch {
            try {
                Log.d("mediaresponse", Gson().toJson(mediaResponse))
                val response = RetrofitPhoto.retrofitMedia.upgenerateImage(
                    authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
                    mediaResponse = mediaResponse
                )
                val uploadResponse = response.body()
                if (response.isSuccessful) {
                    Log.d("Generate", "Generate thành công: ${Gson().toJson(response.body()?.data)}")

                    val mediaUrl = uploadResponse?.data?.firstOrNull()?.original?.url ?: ""
                    val fullImageUrl = if (mediaUrl.isNotBlank()) {
                        "https://short.techres.vn/$mediaUrl"
                    } else {
                        uri.toString()
                    }
                    Log.d("fullimageurl", Gson().toJson(fullImageUrl))

                    Log.d("Generate", "Generate thành công: ${Gson().toJson(response.body()?.data)}")
                    uploadImageAndSendSocket(context, uri, response.body()?.data ?: emptyList(), conversationId)

                } else {
                    Log.e("Generate", "Lỗi: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Generate", "Exception: ${e.message}")
            }
        }
    }


    fun sendTinNhan(message: String) {
        val data = MessageSendModel(
            message = message,
            thumb = Thumb(),
            keyError = randomKey(),
        )
        Log.d("emit data", Gson().toJson(data))
        Log.d("tin nhan gui", "tin nhắn là: ${data.message}")
        Log.d("key_error", "key_error: ${data.keyError}")


        SocketManager.socket?.emit("message-text-v1", JSONObject(Gson().toJson(data)))
    }

    fun startListeningMessages(context: Context) {
        SocketManager.socket?.on("listen-message-text-v1") { args ->
            Log.d("listentext", "ĐÃ NHẬN SỰ KIỆN SOCKET: listen-message-text-v1")
            val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)
            val userIdCache = sharedPref.getInt("user_id", 0)

            val data = args[0] as JSONObject
            Log.d("nhantinnhanok", Gson().toJson(data))

            try {
                val userObject = data.getJSONObject("user")
                val userId = userObject.getInt("user_id")
                val message = data.optString("message", "")
                val createdAt = data.optString("created_at", "")
                val messageType = data.optInt("message_type", 1)

                Log.d("userIdCache", userIdCache.toString())
                Log.d("userId", userId.toString())

                tinNhanList.add(0,
                    TinNhan(
                        isMine = userId == userIdCache,
                        message = message,
                        messageType = messageType,
                        time = createdAt,
                        createdAt = createdAt,
                        groupId = data.optString("conversation_id", ""),
                        imageUrl = null,
                        audioUrl = null,
                        fileName = null,
                        keyError = randomKey(),
                        media = emptyList()
                    )
                )
            } catch (e: Exception) {
                Log.e("MessageTextError", "Lỗi xử lý tin nhắn text: ${e.message}")
            }
        }
    }

    fun randomKey(length: Int = 10): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    fun getMediaInfoFromUri(context: Context, uri: Uri): MediaItem {
        val fileName = queryFileName(context, uri)
        val fileSize = queryFileSize(context, uri)
        val (width, height) = getImageDimensions(context, uri)
        return MediaItem(
            isKeep = 1,
            size = fileSize.toInt(),
            name = fileName,
            width = width,
            type = 0,
            url = uri.toString(),
            height = height,
        )
    }
    fun queryFileName(context: Context, uri: Uri): String {
        var name = "unknown.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = it.getString(index)
            }
        }
        return name
    }
    fun queryFileSize(context: Context, uri: Uri): Long {
        var size: Long = 0
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.SIZE)
                if (index >= 0) size = it.getLong(index)
            }
        }
        return size
    }
    fun getImageDimensions(context: Context, uri: Uri): Pair<Int, Int> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()
        return options.outWidth to options.outHeight
    }

    fun uploadImageAndSendSocket(
        context: Context,
        uri: Uri,
        existingMediaItems: List<MediaItemUpload>,
        conversationId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parts = mutableListOf<MultipartBody.Part>()
                val contentResolver = context.contentResolver

                existingMediaItems.forEachIndexed { index, mediaItem ->
                    val fileName = queryFileName(context, uri)
                    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                    val fileBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }

                    if (fileBytes == null || fileBytes.isEmpty()) {
                        Log.d("uploadImage", "Không có dữ liệu để upload từ InputStream: $uri")
                        return@forEachIndexed
                    }

                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData(
                        name = "medias[$index][file]",
                        filename = fileName,
                        body = requestBody
                    )
                    val mediaIdPart = MultipartBody.Part.createFormData(
                        name = "medias[$index][media_id]",
                        value = mediaItem.mediaId
                    )
                    val typePart = MultipartBody.Part.createFormData(
                        name = "medias[$index][type]",
                        value = "0" // type = 0 cho ảnh
                    )

                    parts.add(filePart)
                    parts.add(mediaIdPart)
                    parts.add(typePart)
                }
                // 3. Gửi multipart upload
                val response = RetrofitPhoto.retrofitMedia.uploadImage(
                    authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
                    medias = parts
                )
                val uploadResponse = response.body()
                Log.d("UploadResponse", "Response: ${Gson().toJson(response.body())}")
                if (response.isSuccessful && uploadResponse != null) {
                    Log.d("UploadDebug", "Upload ảnh thành công")
                    val mediaIds = existingMediaItems.map { it.mediaId }
                    val keyError = randomKey()
                    val emitData = JSONObject().apply {
                        put("conversation_id", conversationId)
                        put("key_error", keyError)
                        put("media", JSONArray(mediaIds))
                    }

                    SocketManager.socket?.emit("message-image-v1", emitData)
                    Log.d("EmitSocket", "Đã emit message-image-v1 với: $emitData")
                } else {
                    Log.d("UploadImage", "Upload không thành công: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.d("uploadImage", "Lỗi Exception: ${e.message}", e)
            }
        }
    }

    fun startListeningImageMessages(context: Context, conversationId: String) {
        SocketManager.socket?.off("listen-message-image-v1")
        SocketManager.socket?.on("listen-message-image-v1") { args ->
            Log.d("listenImageMessage", "ĐÃ NHẬN SỰ KIỆN SOCKET: listen-message-image-v1")
            val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)
            val userIdCache = sharedPref.getInt("user_id", 0)

            val data = args[0] as JSONObject
            Log.d("listenImageMessage", "Data: ${Gson().toJson(data)}")

            try {
                val userObject = data.getJSONObject("user")
                val userId = userObject.getInt("user_id")
                val createdAt = data.optString("created_at", "")
                val messageType = data.optInt("message_type", 2)
                val mediaArray = data.getJSONArray("media")


                if (mediaArray.length() > 0) {
                    val mediaObject = mediaArray.getJSONObject(0)
                    val original = mediaObject.getJSONObject("original")
                    val mediaUrl = original.optString("url", "")
                    val fullImageUrl = if (mediaUrl.isNotBlank()) {
                        "https://short.techres.vn/$mediaUrl"
                    } else { "" }

                    if (mediaUrl.isNotBlank()) {
                        viewModelScope.launch {
                            tinNhanList.add(0, TinNhan(
                                isMine = userId == userIdCache,
                                imageUrl = fullImageUrl,
                                time = createdAt,
                                groupId = conversationId,
                                audioUrl = null,
                                createdAt = "",
                                fileName = null,
                                keyError = randomKey(),
                                media =  emptyList(),
                                messageType = messageType,
                                message = "",
                            ))
                            Log.d("ImageMessageAdded", "Đã thêm tin nhắn hình ảnh từ user $userId: $mediaUrl")
                        }
                    } else {
                        Log.w("ImageMessage", "mediaUrl bị trống hoặc null")
                    }
                } else {
                    Log.w("ImageMessage", "Không có media nào trong tin nhắn")
                }
            } catch (e: Exception) {
                Log.e("ImageMessageError", "Lỗi xử lý socket listen-message-image-v1: ${e.message}")
            }
        }
    }


}



