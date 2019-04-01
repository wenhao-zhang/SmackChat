package com.wenhao.smackchat.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.wenhao.smackchat.Adapater.MessageAdapter
import com.wenhao.smackchat.Model.Channel
import com.wenhao.smackchat.Model.Message
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

    private val socket = IO.socket(SOCKET_URL)

    var selectedChannel: Channel? = null

    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter


    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email

                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                loginBtnNavHeader.text = "Logout"
                userImageNavHeader.setBackgroundColor(UserDataService.getAvatarColor(UserDataService.avatarColor))

                MessageService.getChannels{ complete ->
                    if (complete){

                        if (MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
                toggleVisibility(App.prefs.isLoggedIn)
            }
        }
    }

    private  val onNewMessage = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId: String = args[2] as String

                if (channelId == this.selectedChannel?.id){
                    val message: String = args[0] as String
                    val userName: String = args[3] as String
                    val userAvatar: String = args[4] as String
                    val userAvatarColor: String = args[5] as String
                    val id: String = args[6] as String
                    val timpeStamp: String = args[7] as String

                    val newMessage = Message(message, userName, channelId, userAvatar, userAvatarColor, id, timpeStamp)
                    MessageService.messages.add(newMessage)

                    this.messageAdapter.notifyDataSetChanged()
                    if (this.messageAdapter.itemCount > 0){
                        messageListView.smoothScrollToPosition(this.messageAdapter.itemCount - 1)
                    }

                }
            }
        }
    }

    private val onNewChannel = Emitter.Listener{ args ->
        if(App.prefs.isLoggedIn) {
            runOnUiThread {

                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)

                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        socket.connect()
        socket.on("channelCreated", this.onNewChannel)
        socket.on("messageCreated", this.onNewMessage)

        this.channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
        this.setupAdapter()

        if (App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onResume() {
        super.onResume()
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

        if (App.prefs.isLoggedIn){
            UserDataService.logOut()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Login"
            toggleVisibility(App.prefs.isLoggedIn)

        }else{
            val loginActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginActivityIntent)
        }

    }

    fun addChannelBtnOnClicked(view: View){
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescText)

                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel"){_, _ ->
                }
                .show()
        }
    }

    fun sendMessageBtnOnClicked(view: View){

        if(App.prefs.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null){
            val userId = UserDataService.id
            val userName = UserDataService.name
            val userAvatar  = UserDataService.avatarName
            val userAvatarColor = UserDataService.avatarColor
            val channelId = this.selectedChannel!!.id

            socket.emit("newMessage", messageTextField.text.toString(), userId, channelId,
                userName, userAvatar, userAvatarColor)
            messageTextField.text.clear()
            hideKeyBoard()
        }
    }

    private fun updateWithChannel(){
        mainChannelName.text = "#${this.selectedChannel?.name}"

        if (selectedChannel != null){
            MessageService.getMessages(selectedChannel!!.id){complete ->
                if (complete){
                    this.messageAdapter.notifyDataSetChanged()
                    if (this.messageAdapter.itemCount > 0){
                        messageListView.smoothScrollToPosition(this.messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun toggleVisibility(isVisibe: Boolean){

        val visibility = if (isVisibe) View.VISIBLE else View.INVISIBLE
        sendMessageBtn.visibility = visibility
        messageTextField.visibility = visibility
        addChannelBtn.visibility = visibility
    }

    private fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun setupAdapter(){
        this.channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter


        this.messageAdapter = MessageAdapter(this, MessageService.messages){}
        messageListView.adapter = this.messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager
    }
}
