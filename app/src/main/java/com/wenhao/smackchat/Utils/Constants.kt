package com.wenhao.smackchat.Utils

//API URLs
const val BASE_URL = "https://secure-taiga-99391.herokuapp.com/v1"

const val GET_ALL_CHANNELS_URL = "${BASE_URL}/channel"
const val GET_MESSAGES_URL = "${BASE_URL}/message/byChannel/"

const val AUTH_URL = "${BASE_URL}/account"
const val REGISTER_URL = "${AUTH_URL}/register"
const val LOGIN_URL =  "${AUTH_URL}/login"

const val USER_URL = "${BASE_URL}/user"
const val FIND_BY_EMAIL_URL = "${USER_URL}/byEmail/"
const val CREATE_USER_URL =  "${USER_URL}/add"

const val SOCKET_URL = "https://secure-taiga-99391.herokuapp.com"

//Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"