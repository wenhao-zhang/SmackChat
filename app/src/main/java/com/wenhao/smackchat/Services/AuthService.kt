package com.wenhao.smackchat.Services

import android.content.Context

object AuthService{

    fun registerUser(email: String, password: String, complete: (Boolean) -> Unit){

    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit) {

    }

    fun createUser(name: String, email: String, avatarName: String, avatarColor: String,
                   complete: (Boolean) -> Unit){

    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit){

    }
}