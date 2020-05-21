package com.wenhao.smackchat.Model

import com.squareup.moshi.Json

data class Channel(val name: String, val description: String, @Json(name = "_id") val id: String) {

    override fun toString(): String {
        return "#${this.name}"
    }
}