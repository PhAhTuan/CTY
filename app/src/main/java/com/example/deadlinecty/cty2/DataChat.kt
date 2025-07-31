package com.example.deadlinecty2.data

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
    var isMine: Boolean,
    var text: String? = null,
    var imageUrl: String? = null,
    var audioUrl: String? = null,
    var fileName: String? = null,
    var time: String,
    var groupId: String? = null,

)
