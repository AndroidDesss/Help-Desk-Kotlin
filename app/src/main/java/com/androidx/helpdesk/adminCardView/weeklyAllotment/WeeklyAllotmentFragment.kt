package com.androidx.helpdesk.adminCardView.weeklyAllotment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentWeeklyAllotmentBinding

class WeeklyAllotmentFragment : Fragment() {

    private var binding: FragmentWeeklyAllotmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weekly_allotment, container, false)
        return binding!!.root
    }
}