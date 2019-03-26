package com.wenhao.smackchat.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.wenhao.smackchat.R
import com.wenhao.smackchat.Services.AuthService
import com.wenhao.smackchat.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserSpinner.visibility = View.INVISIBLE
    }

    fun generateColorBtnOnClicked(view: View){
        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)

        createAvatarImageView.setBackgroundColor(Color.rgb(r, g, b))

        this.avatarColor = "[${r.toDouble()/255}, ${g.toDouble()/255}, ${b.toDouble()/255}, 1]"
    }

    fun createUserBtnOnClicked(view: View){

        this.enableSpinner(true)
        val name = createUserNameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){

            AuthService.registerUser(this, email, password){ registerSuccess ->
                if (registerSuccess){
                    AuthService.loginUser(this, email, password){loginSuccess ->
                        if(loginSuccess){
                            AuthService.createUser(this, name, email, this.userAvatar, this.avatarColor){ createSuccess ->
                                if (createSuccess){

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    this.enableSpinner(false)
                                    finish()
                                }else{
                                    this.errorToast()
                                }
                            }
                        }else{
                            this.errorToast()
                        }
                    }
                }else{
                    this.errorToast()
                }
            }
        }else{
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            this.enableSpinner(false)
        }
    }


    fun generateUserAvatar(view: View){
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        this.userAvatar = if (color == 0) "light$avatar" else "dark$avatar"

        val resourceId = resources.getIdentifier(this.userAvatar, "drawable", packageName)

        createAvatarImageView.setImageResource(resourceId)
    }

    private fun errorToast(){
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        this.enableSpinner(false)
    }

    private fun enableSpinner(enable: Boolean){
        createUserSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE

        createUserBtn.isEnabled = enable
        createAvatarImageView.isEnabled = enable
        generateColorBtn.isEnabled = enable
    }
}
