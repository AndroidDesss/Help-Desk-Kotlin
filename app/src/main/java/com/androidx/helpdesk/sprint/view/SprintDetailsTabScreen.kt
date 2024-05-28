package com.androidx.helpdesk.sprint.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.ActivitySprintDetailsTabScreenBinding

class SprintDetailsTabScreen : AppCompatActivity() {

    private var binding: ActivitySprintDetailsTabScreenBinding? = null

    private val mLastClickTime: Long = 0

    private var projectId = 0

    private var sprintId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sprint_details_tab_screen)
        bundleData()
        initListener()
        replaceFragment(PendingTaskFragment.newInstance(projectId,sprintId))
    }
    private fun bundleData()
    {
        val intent = intent
        sprintId = intent.getIntExtra("sprintId", 0)
        projectId = intent.getIntExtra("projectId", 0)
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnPendingTasks.setOnClickListener(onClickListener)
        binding!!.btnSprintDetails.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnPendingTasks ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnPendingTasks.isClickable = false
                if (binding!!.btnSprintDetails.isClickable) {
                } else {
                    binding!!.btnSprintDetails.setClickable(true)
                }

                replaceFragment(PendingTaskFragment.newInstance(projectId,sprintId))

                binding!!.btnPendingTasks.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnPendingTasks.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnSprintDetails.setTextColor(resources.getColor(R.color.white))
                binding!!.btnSprintDetails.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
            R.id.btnSprintDetails ->
            {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return@OnClickListener
                }
                binding!!.btnSprintDetails.isClickable = false

                if (binding!!.btnPendingTasks.isClickable) {
                } else {
                    binding!!.btnPendingTasks.isClickable = true
                }
                replaceFragment(SprintDetailsFragment.newInstance(sprintId))

                binding!!.btnSprintDetails.setTextColor(resources.getColor(R.color.orange))
                binding!!.btnSprintDetails.setBackgroundResource(R.drawable.btn_fragment_background)

                binding!!.btnPendingTasks.setTextColor(resources.getColor(R.color.white))
                binding!!.btnPendingTasks.setBackgroundResource(R.drawable.btn_fragment_bg_transparent)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragMan = supportFragmentManager
        val fragTrans = fragMan!!.beginTransaction()
        fragTrans.replace(R.id.sprintDetailsLayout, fragment, "MY_FRAGMENT")
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragTrans.commit()
    }
}