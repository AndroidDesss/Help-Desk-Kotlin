package com.androidx.helpdesk.adminCardView.unAssigned

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentUnAssignedBinding

class UnAssignedFragment : Fragment() {

    private var binding: FragmentUnAssignedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_assigned, container, false)
        return binding!!.root
    }
}