package com.wenhao.smackchat.Model

data class Message(val message: String,
                   val user: User,
                   val channelId: String,
                   val id: String,
                   val timeStamp: String)