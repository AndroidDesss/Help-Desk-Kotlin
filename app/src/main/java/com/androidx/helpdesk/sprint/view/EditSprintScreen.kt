package com.androidx.helpdesk.sprint.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.volley.toolbox.StringRequest
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.ActivityEditSprintScreenBinding

class EditSprintScreen : AppCompatActivity() {

    private var binding: ActivityEditSprintScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sprint_screen)
        binding!!.cardView.visibility = View.VISIBLE
    }
}