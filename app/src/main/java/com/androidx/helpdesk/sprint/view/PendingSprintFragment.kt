package com.androidx.helpdesk.sprint.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.volley.toolbox.StringRequest
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentPendingSprintBinding

class PendingSprintFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentPendingSprintBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_sprint, container, false)
        firstVisit = true
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        }
        else {
//            getBackLogList()
        }
    }
}