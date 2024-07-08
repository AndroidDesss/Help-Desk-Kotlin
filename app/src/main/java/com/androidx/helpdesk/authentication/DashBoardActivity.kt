package com.androidx.helpdesk.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.assigned.AssignedFragment
import com.androidx.helpdesk.adminCardView.completed.CompletedFragment
import com.androidx.helpdesk.adminCardView.completedTask.CompletedTaskFragment
import com.androidx.helpdesk.adminCardView.taskAllotment.TaskAllotmentFragment
import com.androidx.helpdesk.adminCardView.timesheet.view.TimeSheetFragment
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.TimeSheetTaskAllotmentFragment
import com.androidx.helpdesk.adminCardView.unAssigned.UnAssignedFragment
import com.androidx.helpdesk.adminCardView.weeklyAllotment.WeeklyAllotmentFragment
import com.androidx.helpdesk.adminTimeSheetTask.view.EmployeeTaskUpdate
import com.androidx.helpdesk.backLog.view.BackLogFragment
import com.androidx.helpdesk.board.view.BoardFragment
import com.androidx.helpdesk.closedTickets.view.ClosedTickets
import com.androidx.helpdesk.completedTickets.view.CompletedTickets
import com.androidx.helpdesk.createTickets.ClientTicketCreationActivity
import com.androidx.helpdesk.databinding.ActivityDashBoardBinding
import com.androidx.helpdesk.openTickets.view.OpenTickets
import com.androidx.helpdesk.profile.ProfileActivity
import com.androidx.helpdesk.progressTickets.view.ProgressTickets
import com.androidx.helpdesk.projects.view.ProjectFragment
import com.androidx.helpdesk.sharedStorage.SharedPref
import com.androidx.helpdesk.sprint.view.SprintFragment
import com.androidx.helpdesk.timeSheet.view.TimeSheetList
import com.google.android.material.navigation.NavigationView


class DashBoardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private var binding: ActivityDashBoardBinding? = null

    private var close: ImageView? = null

    private var userProfile: ImageView? = null

    private var userName: TextView? = null

    private var screenName: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val headerView = binding!!.navView.getHeaderView(0)
        screenName = toolbar.findViewById(R.id.screenName)
        userProfile = headerView.findViewById(R.id.userProfile)
        userName = headerView.findViewById(R.id.tvUserName)
        close = headerView.findViewById(R.id.close)

        val toggle = ActionBarDrawerToggle(this, binding!!.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        binding!!.navView.setNavigationItemSelectedListener(this)
        toggle.toolbarNavigationClickListener = View.OnClickListener()
        {
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }

        userName!!.text=SharedPref.getFirstName(this) + " " + SharedPref.getLastName(this)
        initListener()
        hideItem()
        if (SharedPref.getUserType(this).equals("Client") || SharedPref.getUserType(this) == "Client")
        {
            screenName!!.text="Open Tickets"
            replaceFragment(OpenTickets())
        }
        else if (SharedPref.getUserType(this).equals("User") || SharedPref.getUserType(this) == "User")
        {
            screenName!!.setText("Time Sheet")
            replaceFragment(TimeSheetList())
        }
        else if (SharedPref.getUserType(this).equals("Admin") || SharedPref.getUserType(this) == "Admin")
        {
            screenName!!.text = "Time Sheet"
            replaceFragment(EmployeeTaskUpdate())
        }
    }

    private fun initListener() {
        close!!.setOnClickListener(onClickListener)
        userProfile!!.setOnClickListener(onClickListener)
        binding!!.logOut.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.close -> binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            R.id.userProfile -> {
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
                if (SharedPref.getUserType(this).equals("Client") || SharedPref.getUserType(this) == "Client")
                {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                else if (SharedPref.getUserType(this).equals("User") || SharedPref.getUserType(this) == "User")
                {
                }
            }
            R.id.logOut -> {
                startActivity(Intent(this, LoginActivity::class.java))
                SharedPref.setUserLoggedIn(this, false)
            }
        }
    }

    override fun onBackPressed() {
        if (binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            CommonMethod.Companion.showAlertDialog(this, "", "Are you sure you want to exit?", "Yes", "No",
                object : CommonMethod.DialogClickListener {
                    override fun dialogOkBtnClicked(value: String?) {
                        finishAffinity()
                    }

                    override fun dialogNoBtnClicked(value: String?) {}
                }
            )
        }
    }

    private fun hideItem() {
        val nav_Menu = binding!!.navView.menu
        if (SharedPref.getUserType(this).equals("Client")|| SharedPref.getUserType(this) == "Client")
        {
            nav_Menu.findItem(R.id.createTickets).isVisible = true
            nav_Menu.findItem(R.id.openTickets).isVisible = true
            nav_Menu.findItem(R.id.progressTickets).isVisible = true
            nav_Menu.findItem(R.id.closedTickets).isVisible = true
            nav_Menu.findItem(R.id.timeSheet).isVisible = false
            nav_Menu.findItem(R.id.completedTickets).isVisible = true
            nav_Menu.findItem(R.id.projects).isVisible = false
            nav_Menu.findItem(R.id.board).isVisible = false
            nav_Menu.findItem(R.id.adminTimeSheet).isVisible = false
            nav_Menu.findItem(R.id.sprint).isVisible = false
            nav_Menu.findItem(R.id.backLog).isVisible = false
            nav_Menu.findItem(R.id.cardView).isVisible = false

        }
        else if (SharedPref.getUserType(this).equals("User")|| SharedPref.getUserType(this) == "User")
        {
            nav_Menu.findItem(R.id.createTickets).isVisible = false
            nav_Menu.findItem(R.id.openTickets).isVisible = false
            nav_Menu.findItem(R.id.progressTickets).isVisible = false
            nav_Menu.findItem(R.id.closedTickets).isVisible = false
            nav_Menu.findItem(R.id.timeSheet).isVisible = true
            nav_Menu.findItem(R.id.completedTickets).isVisible = false
            nav_Menu.findItem(R.id.projects).isVisible = false
            nav_Menu.findItem(R.id.adminTimeSheet).isVisible = false
            nav_Menu.findItem(R.id.board).isVisible = false
            nav_Menu.findItem(R.id.sprint).isVisible = false
            nav_Menu.findItem(R.id.backLog).isVisible = false
            nav_Menu.findItem(R.id.cardView).isVisible = false
        }
        else if (SharedPref.getUserType(this).equals("Admin")|| SharedPref.getUserType(this) == "Admin")
        {
            nav_Menu.findItem(R.id.createTickets).isVisible = false
            nav_Menu.findItem(R.id.openTickets).isVisible = false
            nav_Menu.findItem(R.id.progressTickets).isVisible = false
            nav_Menu.findItem(R.id.closedTickets).isVisible = false
            nav_Menu.findItem(R.id.completedTickets).isVisible = false
            nav_Menu.findItem(R.id.projects).isVisible = true
            nav_Menu.findItem(R.id.board).isVisible = true
            nav_Menu.findItem(R.id.sprint).isVisible = true
            nav_Menu.findItem(R.id.backLog).isVisible = true
            nav_Menu.findItem(R.id.timeSheet).isVisible = false
            nav_Menu.findItem(R.id.adminTimeSheet).isVisible = true
            nav_Menu.findItem(R.id.cardView).isVisible = true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragMan = supportFragmentManager
        val fragTrans = fragMan.beginTransaction()
        fragTrans.replace(R.id.content, fragment, "MY_FRAGMENT")
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragTrans.commit()
    }

    @SuppressLint("SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.openTickets) {
            SharedPref.setScreenId(this, 1)
            screenName!!.text = "Open Tickets"
            replaceFragment(OpenTickets())
        } else if (id == R.id.progressTickets) {
            SharedPref.setScreenId(this, 2)
            screenName!!.text = "Progress Tickets"
            replaceFragment(ProgressTickets())
        } else if (id == R.id.closedTickets) {
            SharedPref.setScreenId(this, 3)
            screenName!!.text = "Closed Tickets"
            replaceFragment(ClosedTickets())
        } else if (id == R.id.completedTickets) {
            SharedPref.setScreenId(this, 4)
            screenName!!.text = "Completed Tickets"
            replaceFragment(CompletedTickets())
        }else if (id == R.id.timeSheet) {
            SharedPref.setScreenId(this, 9)
            screenName!!.setText("Time Sheet")
            replaceFragment(TimeSheetList())
        }else if (id == R.id.projects) {
            SharedPref.setScreenId(this, 5)
            screenName!!.text = "Projects"
            replaceFragment(ProjectFragment())
        }
        else if (id == R.id.board) {
            SharedPref.setScreenId(this, 6)
            screenName!!.text = "Board"
            replaceFragment(BoardFragment())
        }
        else if (id == R.id.sprint) {
            SharedPref.setScreenId(this, 7)
            screenName!!.text = "Sprint"
            replaceFragment(SprintFragment())
        }
        else if (id == R.id.backLog) {
            SharedPref.setScreenId(this, 8)
            screenName!!.text = "Back Log"
            replaceFragment(BackLogFragment())
        } else if (id == R.id.adminTimeSheet) {
            SharedPref.setScreenId(this, 10)
            screenName!!.text = "Time Sheet"
            replaceFragment(EmployeeTaskUpdate())
        }
        else if (id == R.id.unAssigned) {
            SharedPref.setScreenId(this, 11)
            screenName!!.text = "Un Assigned"
            replaceFragment(UnAssignedFragment())
        }
        else if (id == R.id.assigned) {
            SharedPref.setScreenId(this, 12)
            screenName!!.text = "Assigned"
            replaceFragment(AssignedFragment())
        }
        else if (id == R.id.completed) {
            SharedPref.setScreenId(this, 13)
            screenName!!.text = "Completed"
            replaceFragment(CompletedFragment())
        }
        else if (id == R.id.completedTask) {
            SharedPref.setScreenId(this, 14)
            screenName!!.text = "Completed Task"
            replaceFragment(CompletedTaskFragment())
        }
        else if (id == R.id.taskAllotmentList) {
            SharedPref.setScreenId(this, 15)
            screenName!!.text = "Task Allotment List"
            replaceFragment(TaskAllotmentFragment())
        }
        else if (id == R.id.timeSheetList) {
            SharedPref.setScreenId(this, 16)
            screenName!!.text = "Time Sheet List"
            replaceFragment(TimeSheetFragment())
        }
        else if (id == R.id.taskTimeSheetAllotmentList) {
            SharedPref.setScreenId(this, 17)
            screenName!!.text = "Task Allotment Vs TimeSheet List"
            replaceFragment(TimeSheetTaskAllotmentFragment())
        }
        else if (id == R.id.weeklyAllotmentList) {
            SharedPref.setScreenId(this, 18)
            screenName!!.text = "Weekly Allotment List"
            replaceFragment(WeeklyAllotmentFragment())
        }

        else if (id == R.id.createTickets) {
            startActivity(Intent(this, ClientTicketCreationActivity::class.java))
        }
        binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}