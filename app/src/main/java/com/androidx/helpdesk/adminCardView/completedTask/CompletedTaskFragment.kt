package com.androidx.helpdesk.adminCardView.completedTask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentCompletedTaskBinding

class CompletedTaskFragment : Fragment() {

    private var binding: FragmentCompletedTaskBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_completed_task, container, false)
        return binding!!.root
    }
}