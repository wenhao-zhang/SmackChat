package com.wenhao.smackchat.Adapter.Json

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.wenhao.smackchat.Model.Message;
import com.wenhao.smackchat.Model.Json.MessageJson;
import com.wenhao.smackchat.Model.User

class MessageAdapter{

    @FromJson fun messageFromJson(messageJson: MessageJson) : Message{
        val user: User  = User(messageJson.userId, messageJson.userAvatarColor, messageJson.userAvatar,
                                messageJson.userName)

        return Message(messageJson.message, user, messageJson.channelId, messageJson.id, messageJson.timeStamp)
    }

    @ToJson fun messageToJson(message: Message) : MessageJson {
        val user = message.user

        val userName = user.name
        val userId = user.id
        val userAvatarColor = user.avatarColor
        val userAvatar = user.avatarName

        val messageText = message.message
        val channelId = message.channelId
        val id = message.id
        val timeStamp = message.timeStamp

        return MessageJson(messageText, userName, channelId, userAvatar, userAvatarColor, id, userId, timeStamp)
    }
}