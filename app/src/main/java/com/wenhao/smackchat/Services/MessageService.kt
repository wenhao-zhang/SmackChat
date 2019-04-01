package com.wenhao.smackchat.Services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.wenhao.smackchat.Controller.App
import com.wenhao.smackchat.Model.Channel
import com.wenhao.smackchat.Model.Message
import com.wenhao.smackchat.Utilities.GET_ALL_CHANNELS_URL
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){


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

    fun clearChannels(complete: () -> Unit){
        this.channels.clear()
        complete()
    }
}