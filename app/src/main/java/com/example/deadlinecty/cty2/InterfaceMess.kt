    package com.example.deadlinecty2.data


    import android.net.Uri
    import android.util.Log
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.gestures.detectTransformGestures
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material.icons.filled.Menu
    import androidx.compose.material.icons.filled.Person
    import androidx.compose.material.icons.filled.Search
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.material3.Icon
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.deadlinecty.R
    import androidx.compose.runtime.LaunchedEffect
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavController
    import androidx.navigation.compose.rememberNavController
    import coil.compose.AsyncImage
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.material.icons.automirrored.filled.Send
    import androidx.compose.material.icons.filled.Image
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.TextFieldDefaults
    import androidx.compose.runtime.mutableFloatStateOf
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.input.pointer.pointerInput
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import com.example.deadlinecty.cty2.MediaResponse
    import com.google.gson.Gson
    import org.json.JSONObject


    @Composable
    fun HomeScreenTN(groupChat: DataChat, navController: NavController, messageViewModel: MessageViewModel = viewModel()) {
        val context = LocalContext.current

        LaunchedEffect(groupChat.conversationId) {
            messageViewModel.loadMessagesFromApi(context,groupChat.conversationId)
            messageViewModel.startListeningMessages(context)
            messageViewModel.startListeningImageMessages(context, groupChat.conversationId)
        }

        val json = JSONObject().apply {
            put("conversation_id", groupChat.conversationId)
        }
        SocketManager.socket?.emit("join-room", json)
        Log.d("groupchatOK", groupChat.conversationId)

        SocketManager.socket?.on("socket-error-v1") { args ->
            val data = args[0] as JSONObject
            Log.d("data error", Gson().toJson(data))
        }

        Column(modifier = Modifier.fillMaxSize()) {
            HeadTN(groupChat = groupChat, navController)
            BodyTN(
                tinNhanList = messageViewModel.tinNhanList,
                modifier = Modifier.weight(1f),

                )
            EndTN(messageViewModel, groupChat.conversationId)
        }
    }
    @Composable
    fun HeadTN(groupChat: DataChat, navController: NavController){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFA500))
                .padding(top = 28.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "nut back",
                modifier = Modifier
                    .size(38.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.avt_trang_den),
                contentDescription = "anh gr chat",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)

            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = groupChat.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${groupChat.noOfMember} Member",
                        maxLines = 1,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "tim kiem",
                    modifier = Modifier
                        .size(34.dp)
                        .clickable{},
                    tint = Color.White
                )
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "thanh vien",
                    modifier = Modifier
                        .size(34.dp)
                        .clickable{},
                    tint = Color.White,
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "menu",
                    modifier = Modifier
                        .size(34.dp)
                        .clickable{},
                    tint = Color.White
                )
            }
        }
    }

    @Composable
    fun BodyTN(
        tinNhanList: List<TinNhan>,
        modifier: Modifier = Modifier
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxWidth().background(Color(0xFFF0F0F0)).padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            items(
                items = tinNhanList,
                key = { tin -> tin.keyError ?: tin.createdAt }
            ) { tin ->
                TinNhanItem(tin)
            }
        }
        LaunchedEffect(tinNhanList.size) {
            listState.animateScrollToItem(0)
        }
    }


    @Composable
    fun EndTN(messageViewModel: MessageViewModel, conversationId: String) {
        var sendText by remember { mutableStateOf("") }
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.take(5)?.let { selectedUris ->
                val mediaItems = selectedUris.map { uri ->
                    messageViewModel.getMediaInfoFromUri(context, uri)
                }
                val mediaResponse = MediaResponse(medias = mediaItems)
                messageViewModel.uploadMediaMultiple(mediaResponse, selectedUris, context, conversationId)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)

        ) {
            IconButton(
                onClick = {launcher.launch("image/*")},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Mở ảnh",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(start = 4.dp),
                    tint = Color.Gray
                )
            }
            TextField(
                value = sendText,
                onValueChange = { newText -> sendText = newText },
                placeholder = { Text("Nhập tin nhắn...") },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (sendText.isNotBlank() || imageUri != null) {
                        messageViewModel.sendTinNhan(sendText)
                        sendText = ""
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gửi",
                    modifier = Modifier
                        .size(36.dp),
                    tint = Color.Gray
                )
            }
        }
    }

    @Composable
    fun TinNhanItem(tin: TinNhan) {
        var showTime by remember { mutableStateOf(false) }
        val isMine = tin.isMine

        // ✅ Biến lưu ảnh đang chọn
        var selectedImageUrl by remember { mutableStateOf<String?>(null) }

        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            val messageContent = @Composable {
                when (tin.messageType) {
                    1 -> {
                        tin.message.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = Color.Black)
                        }
                    }
                    2 -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tin.media.take(5).chunked(3).forEach { mediaChunk ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    mediaChunk.forEach { media ->
                                        val fullUrl = getFullMediaUrl(media.original.url)
                                        AsyncImage(
                                            model = fullUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    selectedImageUrl = fullUrl
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        Text("Tin nhắn không xác định", color = Color.Gray)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clickable { showTime = !showTime }
                    .let {
                        if (tin.messageType == 2) it
                        else it.background(
                            if (isMine) Color(0xFFDCF8C6) else Color.White,
                            shape = RoundedCornerShape(8.dp)
                        ).padding(8.dp)
                    }
            ) {
                messageContent()
            }

            if (showTime) {
                Text(
                    text = tin.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            //  Hiển thị overlay khi ảnh được chọn
            if (selectedImageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                ) {
                    var scale by remember { mutableFloatStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }

                    AsyncImage(
                        model = selectedImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 5f)
                                    if (scale == 1f) {
                                        offset = Offset.Zero
                                    } else {
                                        offset += pan
                                    }
                                }
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .clickable { selectedImageUrl = null }
                    )
                }
            }

        }
    }

    fun getFullMediaUrl(rawUrl: String): String {
        Log.d("getfullmedia", Gson().toJson(rawUrl))
        // Nếu rawUrl đã bắt đầu bằng http thì return luôn (tránh ghép sai)
        return if (rawUrl.startsWith("http")) {
            rawUrl
        } else {
            "https://short.techres.vn/$rawUrl"
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ShowHomeScreenTN(){
        val fakeGroupChat = DataChat(
            name = "Nhóm Công ty",
            lastMessage = "Thông báo họp lúc 3h",
            time = "10:45",
            avatarUrl = "",
            noOfNotSeen = 2,
            noOfMember = 5,
            conversationId = "",
        )
        val navController = rememberNavController()
        HomeScreenTN(groupChat = fakeGroupChat, navController)
    }
