package com.androidx.helpdesk.backLog.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.android.volley.toolbox.StringRequest
import com.androidx.helpdesk.R

class NewBackLogScreen : AppCompatActivity() {

    private var binding: ActivityNewBackLogScreenBinding? = null

    private var stringRequest: StringRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_back_log_screen)
    }
}