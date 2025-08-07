package com.example.deadlinecty2.data

import com.example.deadlinecty.cty2.MediaItemUpload
import kotlinx.serialization.SerialName



data class DataChat(
    @SerialName("conversation_id")
    var conversationId: String = "",
    var name: String = "",
    @SerialName("last_message")
    var lastMessage: String = "",
    var time: String = "",
    var avatarUrl: String = "",
    @SerialName("no_of_not_seen")
    var noOfNotSeen: Int = 0,
    @SerialName("no_of_member")
    var noOfMember: Int = 0
)

data class TinNhan(
    var isMine: Boolean = false,
    var message: String = "",
    var imageUrl: String? = null,
    var audioUrl: String? = null,
    var createdAt: String = "",
    var groupId: String? = null,
    var user: MessageUser? = null,
    var fileName: String? = null,
    var keyError: String? = null,
    var media: List<Media> = emptyList(),
    var messageType: Int? = null,
    var time: String = "",
)

