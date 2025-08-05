package com.example.deadlinecty.cty2

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    val medias: List<MediaItem>
)

data class MediaItem(
    @SerializedName("is_keep")
    val isKeep: Int = 1,
    val size: Int,
    val name: String,
    val width: Int,
    val type: Int,
    val url: String,
    val height: Int
)
//-------------------------------------
data class GenerateResponse(
    val status: Int,
    val message: String,
    val data: List<MediaItemUpload>?
)

data class MediaItemUpload(
    @SerializedName("key_error")
    val keyError: String,
    @SerializedName("media_id")
    val mediaId: String,
    val type: Int,
    val original: MediaContent?,
    val thumb: MediaContent?,
    val medium: MediaContent?
)

data class MediaContent(
    val url: String,
    val name: String,
    val size: Int,
    val width: Int,
    val height: Int,
    @SerializedName("link_full")
    val linkFull: String? = null
)

