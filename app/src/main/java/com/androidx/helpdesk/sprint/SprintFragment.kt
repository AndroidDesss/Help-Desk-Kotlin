package com.androidx.helpdesk.sprint

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentSprintBinding
import com.androidx.helpdesk.sprint.view.CurrentSprintFragment
import com.androidx.helpdesk.sprint.view.PendingSprintFragment


class SprintFragment : Fragment() {

    private var binding: FragmentSprintBinding? = null

    private val mLastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sprint, container, false)
        initListener()
        replaceFragment(CurrentSprintFragment())
        return binding!!.root
    }

    private fun initListener() {
        binding!!.btnPendingSprint.setOnClickListener(onClickListener)
        binding!!.btnCurrentSprint.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnPendingSprint ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnPendingSprint.isClickable = false
                if (binding!!.btnCurrentSprint.isClickable) {
                } else {
                    binding!!.btnCurrentSprint.setClickable(true)
                }

                replaceFragment(PendingSprintFragment())

                binding!!.btnPendingSprint.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnPendingSprint.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnCurrentSprint.setTextColor(resources.getColor(R.color.white))
                binding!!.btnCurrentSprint.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
            R.id.btnCurrentSprint ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnCurrentSprint.isClickable = false

                if (binding!!.btnPendingSprint.isClickable) {
                } else {
                    binding!!.btnPendingSprint.isClickable = true
                }
                replaceFragment(CurrentSprintFragment())

                binding!!.btnCurrentSprint.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnCurrentSprint.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnPendingSprint.setTextColor(resources.getColor(R.color.white))
                binding!!.btnPendingSprint.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragMan = activity?.supportFragmentManager
        val fragTrans = fragMan!!.beginTransaction()
        fragTrans.replace(R.id.sprintLayout, fragment, "MY_FRAGMENT")
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragTrans.commit()
    }

}