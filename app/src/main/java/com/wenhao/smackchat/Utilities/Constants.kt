package com.wenhao.smackchat.Utilities

//API URLs
val BASE_URL = "https://secure-taiga-99391.herokuapp.com/v1"

val GET_ALL_CHANNELS_URL = "${BASE_URL}/channel"

val AUTH_URL = "${BASE_URL}/account"
val REGISTER_URL = "${AUTH_URL}/register"
val LOGIN_URL =  "${AUTH_URL}/login"

val USER_URL = "${BASE_URL}/user"
val FIND_BY_EMAIL_URL = "${USER_URL}/byEmail/"
val CREATE_USER_URL =  "${USER_URL}/add"

val SOCKET_URL = "https://secure-taiga-99391.herokuapp.com"

//Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"