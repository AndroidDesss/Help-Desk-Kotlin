package com.androidx.helpdesk.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.ActivitySplashScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref

class SplashScreen : AppCompatActivity() {

    private var binding: ActivitySplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        Handler().postDelayed({ checkForLogin() }, 1500)
    }

    private fun checkForLogin() {
        val isUserLoggedIn = SharedPref.isUserLoggedIn(this)
        if (isUserLoggedIn) {
            moveToDashBoard()
        } else {
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToDashBoard() {
        val intent = Intent(this,DashBoardActivity::class.java)
        startActivity(intent)
        finish()
    }

}