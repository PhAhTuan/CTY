package com.example.deadlinecty2.data

import com.google.gson.annotations.SerializedName

data class GroupChatApiResponse(
    val status: Int,
    val message: String,
    val data: List<GroupChatData>
)

data class GroupChatData(
    @SerializedName("conversation_id")
    val conversationId: String,

    val type: Int,

    @SerializedName("object_type")
    val objectType: Int,

    @SerializedName("restaurant_id")
    val restaurantId: String,

    @SerializedName("supplier_id")
    val supplierId: String,

    @SerializedName("no_of_not_seen")
    val noOfNotSeen: Int,

    @SerializedName("no_of_member")
    val noOfMember: Int,

    @SerializedName("is_send_message")
    val isSendMessage: Int,

    @SerializedName("is_notify")
    val isNotify: Int,

    @SerializedName("is_pinned")
    val isPinned: Int,

    @SerializedName("my_permission")
    val myPermission: Int,

    val position: Long,
    val status: Int,

    @SerializedName("branch_id")
    val branchId: String,

    @SerializedName("brand_id")
    val brandId: String,

    @SerializedName("notify_time")
    val notifyTime: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("last_activity")
    val lastActivity: String,

    @SerializedName("last_message")
    val lastMessage: LastMessage,

    @SerializedName("platform_type")
    val platformType: Int,

    val name: String,
    val avatar: Avatar
)

data class LastMessage(
    val message: String,

    @SerializedName("message_id")
    val messageId: String,

    @SerializedName("message_type")
    val messageType: Int,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_type")
    val userType: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("user_target")
    val userTarget: List<Any>,

    @SerializedName("role_target")
    val roleTarget: List<Any>,

    val tag: List<Any>,

    @SerializedName("user_seen")
    val userSeen: List<UserSeen>,

    val media: List<Media>,
    val sticker: Sticker
)

data class UserSeen(
    @SerializedName("user_id")
    val userId: Int,
    val type: Int,
    val name: String,
    val avatar: String
)

data class Media(
    @SerializedName("media_id")
    val mediaId: String,
    val type: Int,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    val original: ImageInfo,
    val medium: ImageInfo,
    val thumb: ImageInfo
)

data class Sticker(
    @SerializedName("category_sticker_id")
    val categoryStickerId: String,

    @SerializedName("sticker_id")
    val stickerId: String,

    @SerializedName("is_download")
    val isDownload: Int,

    val original: ImageInfo,
    val medium: ImageInfo,
    val thumb: ImageInfo
)

data class Avatar(
    @SerializedName("media_id")
    val mediaId: String,
    val type: Int,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    val original: ImageInfo,
    val medium: ImageInfo,
    val thumb: ImageInfo
)

data class ImageInfo(
    val url: String,
    val name: String,
    val size: Int,
    val width: Int,
    val height: Int,

    @SerializedName("link_full")
    val linkFull: String? = null
)
//----------------------------------------------------
data class MessageApiResponse(
    val status: Int,
    val message: String,
    val data: List<MessageData>
)

data class MessageData(
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("message_type")
    val messageType: Int,
    val message: String,
    val user: MessageUser,
    val conversation: Conversation,
    val position: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("my_reaction")
    val myReaction: Int,
    @SerializedName("no_of_reaction")
    val noOfReaction: Int,
    @SerializedName("no_of_like")
    val noOfLike: Int,
    @SerializedName("no_of_love")
    val noOfLove: Int,
    @SerializedName("no_of_haha")
    val noOfHaha: Int,
    @SerializedName("no_of_wow")
    val noOfWow: Int,
    @SerializedName("no_of_sad")
    val noOfSad: Int,
    @SerializedName("no_of_angry")
    val noOfAngry: Int,
    val media: List<Media>,
    @SerializedName("is_timeline")
    val isTimeline: Int,
    //val is_mine: Boolean = false,
)

data class MessageUser(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val avatar: String
)

data class Conversation(
    @SerializedName("conversation_id")
    val conversationId: String,
    val name: String
)
