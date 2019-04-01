package com.wenhao.smackchat.Adapater

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wenhao.smackchat.Model.Message
import com.wenhao.smackchat.R
import com.wenhao.smackchat.Services.MessageService
import com.wenhao.smackchat.Services.UserDataService

class MessageAdapter(val context: Context, val messages: ArrayList<Message>, val itemClick: (Message) -> Unit): RecyclerView.Adapter<MessageAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return this.messages.count()
    }

    override fun onBindViewHolder(holder: MessageAdapter.Holder, position: Int) {
        return holder.bindMessage(this.messages[position], context)
    }


    inner class  Holder(itemView: View, val itemClick: (Message) -> Unit):RecyclerView.ViewHolder(itemView){

        val userAvatar =itemView.findViewById<ImageView>(R.id.messageUserImage)
        val userName = itemView.findViewById<TextView>(R.id.messageUserNameTxt)
        val timeStamp = itemView.findViewById<TextView>(R.id.messageTimeStampTxt)
        val messageBody = itemView.findViewById<TextView>(R.id.messageBodyTxt)


        fun bindMessage(message: Message, context: Context){
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)

            val backgroundColor = UserDataService.getAvatarColor(message.userAvatarColor)

            this.userAvatar.setImageResource(resourceId)
            this.userAvatar.setBackgroundColor(backgroundColor)
            this.userName.text = message.userName
            this.messageBody.text = message.message
            this.timeStamp.text = MessageService.returnDateString(message.timpeStamp)

        }
    }
}