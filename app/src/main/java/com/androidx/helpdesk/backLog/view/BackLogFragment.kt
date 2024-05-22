package com.androidx.helpdesk.backLog.view

import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.FragmentBackLogBinding

class BackLogFragment : Fragment() {

    private var binding: FragmentBackLogBinding? = null

    private val mLastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_back_log, container, false)
        initListener()
        replaceFragment(UnAssignedBackLogFragment())
        return binding!!.root
    }

    private fun initListener() {
        binding!!.btnUnAssigned.setOnClickListener(onClickListener)
        binding!!.btnAssigned.setOnClickListener(onClickListener)
        binding!!.btnInProgess.setOnClickListener(onClickListener)
        binding!!.btnCompleted.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnUnAssigned ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }

                binding!!.btnUnAssigned.isClickable = false
                if (binding!!.btnAssigned.isClickable || binding!!.btnInProgess.isClickable || binding!!.btnCompleted.isClickable) {
                } else {
                    binding!!.btnAssigned.setClickable(true)
                    binding!!.btnInProgess.setClickable(true)
                    binding!!.btnCompleted.setClickable(true)
                }

                replaceFragment(UnAssignedBackLogFragment())

                binding!!.btnUnAssigned.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnUnAssigned.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnInProgess.setTextColor(resources.getColor(R.color.white))
                binding!!.btnInProgess.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnCompleted.setTextColor(resources.getColor(R.color.white))
                binding!!.btnCompleted.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
            R.id.btnAssigned ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnAssigned.isClickable = false
                if (binding!!.btnUnAssigned.isClickable || binding!!.btnInProgess.isClickable || binding!!.btnCompleted.isClickable) {
                } else {
                    binding!!.btnUnAssigned.setClickable(true)
                    binding!!.btnInProgess.setClickable(true)
                    binding!!.btnCompleted.setClickable(true)
                }
                replaceFragment(AssignedBackLogFragment())

                binding!!.btnAssigned.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnAssigned.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnUnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnUnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnInProgess.setTextColor(resources.getColor(R.color.white))
                binding!!.btnInProgess.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnCompleted.setTextColor(resources.getColor(R.color.white))
                binding!!.btnCompleted.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
            R.id.btnInProgess ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnInProgess.isClickable = false
                if (binding!!.btnAssigned.isClickable || binding!!.btnUnAssigned.isClickable || binding!!.btnCompleted.isClickable) {
                } else {
                    binding!!.btnAssigned.setClickable(true)
                    binding!!.btnUnAssigned.setClickable(true)
                    binding!!.btnCompleted.setClickable(true)
                }
                replaceFragment(InProgressBackLogFragment())

                binding!!.btnInProgess.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnInProgess.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnUnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnUnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnCompleted.setTextColor(resources.getColor(R.color.white))
                binding!!.btnCompleted.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
            R.id.btnCompleted ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnCompleted.isClickable = false
                if (binding!!.btnAssigned.isClickable || binding!!.btnInProgess.isClickable || binding!!.btnUnAssigned.isClickable) {
                } else {
                    binding!!.btnAssigned.setClickable(true)
                    binding!!.btnInProgess.setClickable(true)
                    binding!!.btnUnAssigned.setClickable(true)
                }
                replaceFragment(CompletedBackLogFragment())

                binding!!.btnCompleted.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnCompleted.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnUnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnUnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnInProgess.setTextColor(resources.getColor(R.color.white))
                binding!!.btnInProgess.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)

                binding!!.btnAssigned.setTextColor(resources.getColor(R.color.white))
                binding!!.btnAssigned.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragMan = activity?.supportFragmentManager
        val fragTrans = fragMan!!.beginTransaction()
        fragTrans.replace(R.id.backLogLayout, fragment, "MY_FRAGMENT")
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragTrans.commit()
    }



}