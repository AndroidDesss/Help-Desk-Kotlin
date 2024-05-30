package com.androidx.helpdesk.board.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.androidx.helpdesk.R
import com.androidx.helpdesk.databinding.ActivityBoardSprintScreenBinding
import com.androidx.helpdesk.sprint.view.CurrentSprintFragment
import com.androidx.helpdesk.sprint.view.PendingSprintFragment

class BoardSprintScreen : AppCompatActivity() {

    private var binding: ActivityBoardSprintScreenBinding? = null

    private val mLastClickTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_sprint_screen)
        initListener()
        replaceFragment(CurrentSprintFragment())
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnPendingSprint.setOnClickListener(onClickListener)
        binding!!.btnCurrentSprint.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
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
        val fragMan = supportFragmentManager
        val fragTrans = fragMan.beginTransaction()
        fragTrans.replace(R.id.boardSprintLayout, fragment, "MY_FRAGMENT")
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragTrans.commit()
    }
}