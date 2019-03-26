package com.wenhao.smackchat.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.wenhao.smackchat.Model.Channel
import com.wenhao.smackchat.R
import com.wenhao.smackchat.Services.AuthService
import com.wenhao.smackchat.Services.MessageService
import com.wenhao.smackchat.Services.UserDataService
import com.wenhao.smackchat.Utilities.BROADCAST_USER_DATA_CHANGE
import com.wenhao.smackchat.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    private val onNewchannel = Emitter.Listener{args ->
        runOnUiThread{
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)

            MessageService.channels.add(newChannel)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        hideKeyBoard()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

    }

    override fun onResume() {
        super.onResume()
        socket.connect()
        socket.on("channelCreated", onNewchannel)
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
        IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderOnClicked(view: View){

        if (AuthService.isLoggedIn){
            UserDataService.logOut()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
            setAddChannelBtnVisibilty(AuthService.isLoggedIn)
            setChatBoxVisibilty(AuthService.isLoggedIn)
        }else{
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginActivityIntent)
        }

    }

    fun addChannelBtnOnClicked(view: View){
        if (AuthService.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescText)

                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel"){dialog: DialogInterface?, which: Int ->
                }
                .show()
        }
    }

    fun sendMessageBtnOnClicked(view: View){

    }

    private fun setAddChannelBtnVisibilty(isVisibe: Boolean){

        val visibility = if (isVisibe) View.VISIBLE else View.INVISIBLE
        sendMessageBtn.visibility = visibility
        messageTextField.visibility = visibility
    }

    private fun setChatBoxVisibilty(isVisibe: Boolean){

        val visibility = if (isVisibe) View.VISIBLE else View.INVISIBLE
        addChannelBtn.visibility = visibility
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                loginBtnNavHeader.text = "Logout"
                userImageNavHeader.setBackgroundColor(UserDataService.getAvatarColor(UserDataService.avatarColor))
            }
            setAddChannelBtnVisibilty(AuthService.isLoggedIn)
            setChatBoxVisibilty(AuthService.isLoggedIn)
        }
    }

    private fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
