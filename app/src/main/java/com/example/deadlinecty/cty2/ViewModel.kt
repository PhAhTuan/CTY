        package com.example.deadlinecty2.data


        import android.R.attr.name
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
                                    val lastMessageText = when (item.lastMessage.messageType) {
                                        1 -> item.lastMessage.message.ifBlank { "Không có tin nhắn" }
                                        2 -> " ${item.lastMessage.userName} đã gửi một hình ảnh"
                                        3 -> " ${item.lastMessage.userName} đã gửi một video"
                                        4 -> " ${item.lastMessage.userName} đã gửi một file"
                                        5 -> " ${item.lastMessage.userName} đã gửi một sticker"
                                        else -> "Tin nhắn mới"
                                    }
                                    Log.d("type message", Gson().toJson(item.lastMessage.messageType))
                                    DataChat(
                                        conversationId = item.conversationId  ,
                                        name = item.name ,
                                        lastMessage = lastMessageText,
                                        time = item.lastActivity ,
                                        avatarUrl = item.avatar.thumb.url,
                                        noOfNotSeen = item.noOfNotSeen ,
                                        noOfMember = item.noOfMember,
                                        avatar = item.avatar
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
                                    user = messageData.user
                                )
                            }
                        )

                        Log.d("tinnhanlist", Gson().toJson(tinNhanList))
                    } catch (e: Exception) {
                        Log.e("MessageViewModel", "Error loading messages: ${e.message}")
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
                SocketManager.socket?.off("listen-message-text-v1")
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
                        val messageUser = MessageUser(
                            userId = userId,
                            name = userObject.optString("username", ""),
                            avatar = userObject.optString("avatar", "")
                        )

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
                                media = emptyList(),
                                user = messageUser
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
                Log.d("getMediaInfoFromUri", "Uri: $uri")
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
                Log.d("queryFileName", "Query tên file từ Uri: $uri -> $name")
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
                    Log.d("queryFileSize", "Query dung lượng file từ Uri: $uri ")
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
                if (inputStream == null) {
                    Log.e("getImageDimensions", "Không mở được inputStream từ Uri: $uri")
                }
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()
                return options.outWidth to options.outHeight
            }


            fun startListeningImageMessages(context: Context, conversationId: String) {
                SocketManager.socket?.off("listen-message-image-v1")
                SocketManager.socket?.on("listen-message-image-v1") { args ->
                    Log.d("listenImageMessage", "✅ ĐÃ NHẬN SỰ KIỆN SOCKET: listen-message-image-v1")
                    val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)
                    val userIdCache = sharedPref.getInt("user_id", 0)

                    val data = args[0] as JSONObject
                    Log.d("listenImageMessage", "Data nhận được: ${data.toString(2)}")

                    try {
                        val userObject = data.getJSONObject("user")
                        val userId = userObject.getInt("user_id")
                        val createdAt = data.optString("created_at", "")
                        val messageType = data.optInt("message_type", 2)
                        val mediaArray = data.getJSONArray("media")
                        val keyError = data.optString("key_error", randomKey())
                        // Bắt đầu parse dữ liệu media theo cấu trúc mới
                        val mediaList = mutableListOf<Media>()

                        for (i in 0 until mediaArray.length()) {
                            val mediaObject = mediaArray.getJSONObject(i)

                            // Hàm phụ để parse một đối tượng JSON thành ImageInfo
                            fun parseImageInfo(json: JSONObject?): ImageInfo {
                                val defaultJson = JSONObject()
                                val obj = json ?: defaultJson
                                return ImageInfo(
                                    url = obj.optString("url"),
                                    name = obj.optString("name"),
                                    size = obj.optInt("size"),
                                    width = obj.optInt("width"),
                                    height = obj.optInt("height"),
                                )
                            }
                            // Parse các đối tượng con
                            val originalInfo = parseImageInfo(mediaObject.optJSONObject("original"))
                            val mediumInfo = parseImageInfo(mediaObject.optJSONObject("medium"))
                            val thumbInfo = parseImageInfo(mediaObject.optJSONObject("thumb"))

                            // Tạo đối tượng Media hoàn chỉnh
                            val mediaItem = Media(
                                mediaId = mediaObject.optString("media_id"),
                                type = mediaObject.optInt("type"),
                                content = mediaObject.optString("content"),
                                createdAt = mediaObject.optString("created_at"),
                                original = originalInfo,
                                medium = mediumInfo,
                                thumb = thumbInfo
                            )
                            mediaList.add(mediaItem)
                        }
                        // Chỉ thêm tin nhắn nếu danh sách media không rỗng
                        if (mediaList.isNotEmpty()) {
                            viewModelScope.launch {
                                val newTinNhan = TinNhan(
                                    isMine = userId == userIdCache,
                                    time = createdAt,
                                    createdAt = createdAt,
                                    groupId = conversationId,
                                    keyError = keyError,
                                    media = mediaList, // Sử dụng danh sách media đã parse
                                    messageType = messageType,
                                    message = ""
                                )
                                tinNhanList.add(0, newTinNhan)
                                Log.d("ImageMessageAdded", "Đã thêm tin nhắn hình ảnh với ${mediaList.size} media.")
                            }
                        } else {
                            Log.w("ImageMessage", "Không có media nào hợp lệ trong tin nhắn nhận được.")
                        }

                    } catch (e: Exception) {
                        Log.e("ImageMessageError", "Lỗi xử lý socket listen-message-image-v1: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
            fun uploadMediaMultiple(mediaResponse: MediaResponse, uris: List<Uri>, context: Context, conversationId: String) {
                viewModelScope.launch {
                    try {
                        val response = RetrofitPhoto.retrofitMedia.upgenerateImage(
                            authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
                            mediaResponse = mediaResponse
                        )

                        if (response.isSuccessful) {
                            val mediaItemsUpload = response.body()?.data ?: emptyList()
                            uploadMultipleMediaAndSendSocket(context, uris, mediaItemsUpload, conversationId, 2, "message-image-v1")
                        }
                    } catch (e: Exception) {
                        Log.e("uploadMediaMultiple", "Lỗi: ${e.message}")
                    }
                }
            }

    fun uploadMultipleMediaAndSendSocket(
        context: Context,
        uris: List<Uri>,
        mediaItemsUpload: List<MediaItemUpload>,
        conversationId: String,
        type: Int,
        socketEvent: String // "message-image-v1" hoặc "message-video-v1"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parts = mutableListOf<MultipartBody.Part>()
                val contentResolver = context.contentResolver

                uris.forEachIndexed { index, uri ->
                    val mediaItem = mediaItemsUpload.getOrNull(index) ?: return@forEachIndexed
                    val fileName = queryFileName(context, uri)
                    val mimeType = contentResolver.getType(uri) ?: if (type == 2) "image/jpeg" else "video/mp4"
                    val fileBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@forEachIndexed

                    val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("medias[$index][file]", fileName, requestBody)
                    val mediaIdPart = MultipartBody.Part.createFormData("medias[$index][media_id]", mediaItem.mediaId)
                    val typePart = MultipartBody.Part.createFormData("medias[$index][type]", type.toString())

                    parts.add(filePart)
                    parts.add(mediaIdPart)
                    parts.add(typePart)
                }

                val response = RetrofitPhoto.retrofitMedia.uploadImage(
                    authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
                    medias = parts
                )

                if (response.isSuccessful) {
                    val mediaIds = mediaItemsUpload.map { it.mediaId }
                    val keyError = randomKey()

                    val emitData = JSONObject().apply {
                        put("conversation_id", conversationId)
                        put("key_error", keyError)
                        put("media", JSONArray(mediaIds))
                    }

                    SocketManager.socket?.emit(socketEvent, emitData)
                    Log.d("EmitSocket", "Gửi media type=$type: $emitData")
                } else {
                    Log.e("UploadMedia", "Upload không thành công: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UploadMedia", "Lỗi: ${e.message}")
            }
        }
    }
            //-----------------------------
            fun uploadMediaMultipleVideo(mediaResponse: MediaResponse, uris: List<Uri>, context: Context, conversationId: String) {
                viewModelScope.launch {
                    try {
                        val response = RetrofitPhoto.retrofitMedia.upgenerateImage(
                            authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
                            mediaResponse = mediaResponse
                        )

                        if (response.isSuccessful) {
                            val mediaItemsUpload = response.body()?.data ?: emptyList()
                            uploadMultipleMediaAndSendSocket(context, uris, mediaItemsUpload, conversationId, 3, "message-video-v1")

                        }
                    } catch (e: Exception) {
                        Log.e("uploadMediaMultipleVideo", "Lỗi: ${e.message}")
                    }
                }
            }


            fun startListeningVideoMessages(context: Context, conversationId: String) {
                SocketManager.socket?.off("listen-message-video-v1")
                SocketManager.socket?.on("listen-message-video-v1") { args ->
                    Log.d("listenVideoMessage", "✅ ĐÃ NHẬN SỰ KIỆN SOCKET: listen-message-video-v1")
                    val sharedPref = context.getSharedPreferences("myAppCache", Context.MODE_PRIVATE)
                    val userIdCache = sharedPref.getInt("user_id", 0)

                    val data = args[0] as JSONObject
                    Log.d("listenVideoMessage", "Data nhận được: ${data.toString(2)}")

                    try {
                        val userObject = data.getJSONObject("user")
                        val userId = userObject.getInt("user_id")
                        val createdAt = data.optString("created_at", "")
                        val messageType = data.optInt("message_type", 3)
                        val mediaArray = data.getJSONArray("media")
                        val keyError = data.optString("key_error", randomKey())

                        val mediaList = mutableListOf<Media>()

                        for (i in 0 until mediaArray.length()) {
                            val mediaObject = mediaArray.getJSONObject(i)
                            fun parseImageInfo(json: JSONObject?): ImageInfo {
                                val defaultJson = JSONObject()
                                val obj = json ?: defaultJson
                                return ImageInfo(
                                    url = obj.optString("url"),
                                    name = obj.optString("name"),
                                    size = obj.optInt("size"),
                                    width = obj.optInt("width"),
                                    height = obj.optInt("height"),
                                )
                            }

                            val originalInfo = parseImageInfo(mediaObject.optJSONObject("original"))
                            val mediumInfo = parseImageInfo(mediaObject.optJSONObject("medium"))
                            val thumbInfo = parseImageInfo(mediaObject.optJSONObject("thumb"))

                            val mediaItem = Media(
                                mediaId = mediaObject.optString("media_id"),
                                type = mediaObject.optInt("type"),
                                content = mediaObject.optString("content"),
                                createdAt = mediaObject.optString("created_at"),
                                original = originalInfo,
                                medium = mediumInfo,
                                thumb = thumbInfo
                            )
                            mediaList.add(mediaItem)
                        }

                        if (mediaList.isNotEmpty()) {
                            viewModelScope.launch {
                                val newTinNhan = TinNhan(
                                    isMine = userId == userIdCache,
                                    time = createdAt,
                                    createdAt = createdAt,
                                    groupId = conversationId,
                                    keyError = keyError,
                                    media = mediaList,
                                    messageType = messageType,
                                    message = ""
                                )
                                tinNhanList.add(0, newTinNhan)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("VideoMessageError", "Lỗi xử lý socket listen-message-video-v1: ${e.message}")
                    }
                }
            }


        }













        //        fun uploadMultipleVideosAndSendSocket(
        //            context: Context,
        //            uris: List<Uri>,
        //            mediaItemsUpload: List<MediaItemUpload>,
        //            conversationId: String
        //        ) {
        //            viewModelScope.launch(Dispatchers.IO) {
        //                try {
        //                    val parts = mutableListOf<MultipartBody.Part>()
        //                    val contentResolver = context.contentResolver
        //
        //                    uris.forEachIndexed { index, uri ->
        //                        val mediaItem = mediaItemsUpload.getOrNull(index) ?: return@forEachIndexed
        //                        val fileName = queryFileName(context, uri)
        //                        val mimeType = contentResolver.getType(uri) ?: "video/mp4"
        //                        val fileBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@forEachIndexed
        //
        //                        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        //                        val filePart = MultipartBody.Part.createFormData("medias[$index][file]", fileName, requestBody)
        //                        val mediaIdPart = MultipartBody.Part.createFormData("medias[$index][media_id]", mediaItem.mediaId)
        //                        val typePart = MultipartBody.Part.createFormData("medias[$index][type]", "2") // ✅ Video type = 2
        //
        //                        parts.add(filePart)
        //                        parts.add(mediaIdPart)
        //                        parts.add(typePart)
        //                    }
        //
        //                    val response = RetrofitPhoto.retrofitMedia.uploadImage(
        //                        authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
        //                        medias = parts
        //                    )
        //
        //                    if (response.isSuccessful) {
        //                        val mediaIds = mediaItemsUpload.map { it.mediaId }
        //                        val keyError = randomKey()
        //
        //                        val emitData = JSONObject().apply {
        //                            put("conversation_id", conversationId)
        //                            put("key_error", keyError)
        //                            put("media", JSONArray(mediaIds))
        //                        }
        //
        //                        SocketManager.socket?.emit("message-video-v1", emitData) // ✅ Socket gửi video
        //                        Log.d("EmitSocketVideo", "Gửi nhiều video: $emitData")
        //                    } else {
        //                        Log.e("UploadMultipleVideos", "Upload không thành công: ${response.errorBody()?.string()}")
        //                    }
        //                } catch (e: Exception) {
        //                    Log.e("UploadMultipleVideos", "Lỗi: ${e.message}")
        //                }
        //            }
        //        }


        //        fun uploadMultipleImagesAndSendSocket(
        //            context: Context,
        //            uris: List<Uri>,
        //            mediaItemsUpload: List<MediaItemUpload>,
        //            conversationId: String
        //        ) {
        //            viewModelScope.launch(Dispatchers.IO) {
        //                try {
        //                    val parts = mutableListOf<MultipartBody.Part>()
        //                    val contentResolver = context.contentResolver
        //
        //                    uris.forEachIndexed { index, uri ->
        //                        val mediaItem = mediaItemsUpload.getOrNull(index) ?: return@forEachIndexed
        //                        val fileName = queryFileName(context, uri)
        //                        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        //                        val fileBytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@forEachIndexed
        //
        //                        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        //                        val filePart = MultipartBody.Part.createFormData("medias[$index][file]", fileName, requestBody)
        //                        val mediaIdPart = MultipartBody.Part.createFormData("medias[$index][media_id]", mediaItem.mediaId)
        //                        val typePart = MultipartBody.Part.createFormData("medias[$index][type]", "0")
        //
        //                        parts.add(filePart)
        //                        parts.add(mediaIdPart)
        //                        parts.add(typePart)
        //                    }
        //
        //                    val response = RetrofitPhoto.retrofitMedia.uploadImage(
        //                        authorization = "Bearer 028a2bb7-227c-4581-85d3-257478e0e4b0",
        //                        medias = parts
        //                    )
        //
        //                    if (response.isSuccessful) {
        //                        val mediaIds = mediaItemsUpload.map { it.mediaId }
        //                        val keyError = randomKey()
        //
        //                        val emitData = JSONObject().apply {
        //                            put("conversation_id", conversationId)
        //                            put("key_error", keyError)
        //                            put("media", JSONArray(mediaIds))
        //                        }
        //
        //                        SocketManager.socket?.emit("message-image-v1", emitData)
        //                        Log.d("EmitSocket", "Gửi nhiều ảnh: $emitData")
        //                    } else {
        //                        Log.e("UploadMultipleImages", "Upload không thành công: ${response.errorBody()?.string()}")
        //                    }
        //                } catch (e: Exception) {
        //                    Log.e("UploadMultipleImages", "Lỗi: ${e.message}")
        //                }
        //            }
        //        }
