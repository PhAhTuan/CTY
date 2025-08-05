package com.example.deadlinecty2.data

import com.google.gson.annotations.SerializedName

data class MessageSendModel(
        @SerializedName("code")
        val code: String = "",

        @SerializedName("create_at")
        val createAt: String = "",

        @SerializedName("link")
        val link: List<String> = emptyList(),

        @SerializedName("link_join_group")
        val linkJoinGroup: String = "",

        @SerializedName("media")
        val media: List<String> = emptyList(),

        @SerializedName("message_reply_id")
        val messageReplyId: String = "",

        @SerializedName("message_vote_id")
        val messageVoteId: String = "",

        @SerializedName("order_id")
        val orderId: String = "",

        @SerializedName("order_platform")
        val orderPlatform: Int = 0,

        @SerializedName("received_at")
        val receivedAt: String = "",

        @SerializedName("sticker_id")
        val stickerId: String = "",

        @SerializedName("tag")
        val tag: List<String> = emptyList(),

        val message: String,

        @SerializedName("key_error")
        val keyError: String,

        @SerializedName("thumb")
        val thumb: Thumb,

        val mediaId: String? = null
)

data class Thumb(
        @SerializedName("domain")
        var domain: String = "",

        @SerializedName("title")
        var title: String = "",

        @SerializedName("description")
        var description: String = "",

        @SerializedName("logo")
        var logo: String = "",

        @SerializedName("url")
        var url: String = "",

        @SerializedName("is_thumb")
        var isThumb: Int =0

)