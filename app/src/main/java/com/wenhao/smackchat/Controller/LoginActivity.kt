package com.wenhao.smackchat.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wenhao.smackchat.R
import com.wenhao.smackchat.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginBtnOnClicked(view: View){
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        this.enableSpinner(true)
        this.hideKeyBoard()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(email, password) { loginSucess ->
                if (loginSucess) {
                    AuthService.findUserByEmail(this) { findUserSuccess ->

                        if (findUserSuccess) {
                            this.enableSpinner(false)
                            finish()
                        } else {
                            this.errorToast()
                        }
                    }
                } else {
                    this.errorToast()
                }
            }
        }else{
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            this.enableSpinner(false)
        }
    }

    fun loginSignUpBtnOnClicked(view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }

    private fun errorToast(){
        Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_LONG).show()
        this.enableSpinner(false)
    }

    private fun enableSpinner(enable: Boolean){
        loginSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE

        loginLoginBtn.isEnabled = !enable
        loginSignUpBtn.isEnabled = !enable

    }

    private fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
