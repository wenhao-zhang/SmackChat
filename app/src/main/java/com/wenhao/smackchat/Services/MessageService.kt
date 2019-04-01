package com.wenhao.smackchat.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.wenhao.smackchat.Controller.App
import com.wenhao.smackchat.Model.Channel
import com.wenhao.smackchat.Model.Message
import com.wenhao.smackchat.Utilities.GET_ALL_CHANNELS_URL
import com.wenhao.smackchat.Utilities.GET_MESSAGES_URL
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){

        clearChannels()

        val channelsRequest = object : JsonArrayRequest(Method.GET, GET_ALL_CHANNELS_URL, null, Response.Listener {response ->

            try{
                for (i in 0 until response.length()){
                    val channel = response.getJSONObject(i)

                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val id = channel.getString("_id")

                    val newChannel = Channel(name, desc, id)

                    this.channels.add(newChannel)
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener {error ->

            Log.d("Error", "Could not retrieve channels: $error")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }


            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers.put("Authorization", "Bearer ${App.prefs.authToken}")

                return headers
            }
        }

        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit){

        val url = "$GET_MESSAGES_URL$channelId"
        clearMessages()

        val messageRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            try{
                for (i in 0 until response.length()){

                    val message = response.getJSONObject(i)

                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timpeStamp = message.getString("timeStamp")

                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timpeStamp)

                    messages.add(newMessage)
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }


        },Response.ErrorListener { error ->
            Log.d("Error", "Could not retrieve channels: $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers.put("Authorization", "Bearer ${App.prefs.authToken}")

                return headers
            }
        }

        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearChannels() {
        this.channels.clear()
    }

    fun clearMessages(){
        this.messages.clear()
    }
}