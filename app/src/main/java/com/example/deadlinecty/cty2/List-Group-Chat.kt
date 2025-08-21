package com.example.deadlinecty.cty2

import  android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deadlinecty2.data.DataChat
import com.example.deadlinecty2.data.HomeViewModel
import com.example.deadlinecty.R
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreenMess(navController: NavController, viewModel: HomeViewModel) {
    val groupList = viewModel.groupList
    LaunchedEffect(Unit) {
        viewModel.loadGroupChats()
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .padding(top = 32.dp)
    ) {
        items(groupList) { groupChat ->
            GroupChatMess(groupChat = groupChat) {
                Log.d("NAVIGATION", "Navigating to groupChatDetail/${groupChat.conversationId}")
                navController.navigate("groupChatDetail/${groupChat.conversationId}")
            }
        }
    }
}
@Composable
fun GroupChatMess(groupChat: DataChat, onClick: () -> Unit) {
    val linkAvt = "https://short.techres.vn/"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = linkAvt + groupChat.avatar.original.url,
            contentDescription = "Anh nhom chat",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.avt_trang_den),
            error = painterResource(R.drawable.avt_trang_den)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = groupChat.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatChatTime(groupChat.time),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                Log.d("time", Gson().toJson(groupChat.time))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = groupChat.lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = groupChat.noOfNotSeen.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
                Image(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Chuông thông báo",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


fun formatChatTime(apiTime: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = parser.parse(apiTime) ?: return apiTime

        val now = Calendar.getInstance()
        val messageTime = Calendar.getInstance().apply { time = date }

        return when {
            // Nếu cùng ngày hôm nay → chỉ hiển thị giờ:phút
            now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) -> {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
            }

            // Nếu là hôm qua → hiển thị chữ "Hôm qua"
            now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) - messageTime.get(Calendar.DAY_OF_YEAR) == 1 -> {
                "Hôm qua"
            }

            // Còn lại → hiển thị ngày/tháng
            else -> {
                SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
            }
        }
    } catch (e: Exception) {
        apiTime // fallback nếu parse lỗi
    }
}


