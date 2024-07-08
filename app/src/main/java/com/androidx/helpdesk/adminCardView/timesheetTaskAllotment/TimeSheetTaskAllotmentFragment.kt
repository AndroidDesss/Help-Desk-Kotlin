package com.androidx.helpdesk.adminCardView.timesheetTaskAllotment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentTimeSheetTaskAllotmentBinding

class TimeSheetTaskAllotmentFragment : Fragment() {

    private var binding: FragmentTimeSheetTaskAllotmentBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_sheet_task_allotment, container, false)
        return binding!!.root
    }
}