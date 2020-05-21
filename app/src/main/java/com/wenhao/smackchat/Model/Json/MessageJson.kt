package com.wenhao.smackchat.Model.Json

import com.squareup.moshi.Json

data class MessageJson(val message: String,
                       val userName: String,
                       val channelId: String,
                       val userAvatar: String,
                       val userAvatarColor: String,
                       @Json(name = "_id") val id: String,
                       val userId: String,
                       val timeStamp: String)