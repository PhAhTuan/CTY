package com.example.deadlinecty2.data

import com.google.gson.annotations.SerializedName

data class MessageSendModel(
        @SerializedName("code")
        val code: String = "",

        @SerializedName("create_at")
        val create_at: String = "",

        @SerializedName("link")
        val link: List<String> = emptyList(),

        @SerializedName("link_join_group")
        val link_join_group: String = "",

        @SerializedName("media")
        val media: List<String> = emptyList(),

        @SerializedName("message_reply_id")
        val message_reply_id: String = "",

        @SerializedName("message_vote_id")
        val message_vote_id: String = "",

        @SerializedName("order_id")
        val order_id: String = "",

        @SerializedName("order_platform")
        val order_platform: Int = 0,

        @SerializedName("received_at")
        val received_at: String = "",

        @SerializedName("sticker_id")
        val sticker_id: String = "",

        @SerializedName("tag")
        val tag: List<String> = emptyList(),

        val message: String,

        @SerializedName("key_error")
        val key_error: String,

        @SerializedName("thumb")
        val thumb: Thumb,

        val media_id: String? = null
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