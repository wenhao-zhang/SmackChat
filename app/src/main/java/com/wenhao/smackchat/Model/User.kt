package com.wenhao.smackchat.Model

import com.squareup.moshi.Json

data class User(@Json(name = "_id") val id: String,
                val avatarColor: String,
                val avatarName: String,
                val name: String,
                val email: String = "")