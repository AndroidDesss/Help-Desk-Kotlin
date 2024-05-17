package com.androidx.helpdesk.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentBoardBinding


class BoardFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentBoardBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)
        firstVisit = true
        return binding!!.getRoot()
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
//            getIssuesList()
        }
    }


}