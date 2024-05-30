package com.androidx.helpdesk.board.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.board.adapter.BoardMembersAdapter
import com.androidx.helpdesk.board.model.AvailableBoardMembersAdapter
import com.androidx.helpdesk.board.model.AvailableBoardMembersModel
import com.androidx.helpdesk.board.model.BoardMembersModel
import com.androidx.helpdesk.databinding.ActivityBoardMembersScreenBinding
import com.androidx.helpdesk.quickProject.`interface`.OnDeleteListener
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class BoardMembersScreen : AppCompatActivity(), OnProgrammerListener, OnDeleteListener {

    private var binding: ActivityBoardMembersScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var boardId = 0

    private var boardMemberId = 0

    private var availableEmployeeId = 0

    private var availableEmployeeName: String? = null

    private var boardMemberName: String? = null

    private var boardMembers: String? = null

    private var boardMemberRole: String? = null

    private var errorMsg: String? = null

    private var errMsg: String? = null

    private var boardMembersList: MutableList<BoardMembersModel> = ArrayList()

    private var boardMembersAdapter: BoardMembersAdapter? = null

    private var availableBoardMembersList: MutableList<AvailableBoardMembersModel> = ArrayList()

    private var availableBoardMembersAdapter: AvailableBoardMembersAdapter? = null

    private var employeeIdDeleteList = ArrayList<Int>()

    private var employeeIdList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_members_screen)
        initListener()
        getBundleData()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.assignMembers.setOnClickListener(onClickListener)
        binding!!.btnRemove.setOnClickListener(onClickListener)
    }

    private fun getBundleData() {
        val mIntent = intent
        boardId = mIntent.getIntExtra("boardId", 0)
        projectId = mIntent.getIntExtra("projectId", 0)
        getBoardMembers()
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.assignMembers ->
            {
                getBoardAvailableProgrammers(view)
            }
            R.id.btnRemove ->
            {
                if(employeeIdDeleteList.isEmpty() || employeeIdDeleteList==null)
                {
                    CommonMethod.showToast(this, "Please Select the Programmers..!")
                }
                else
                {
                    deleteBoardMembers(employeeIdDeleteList)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBoardMembers()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        boardMembersList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getBoardMembersList +  boardId,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                boardMemberId = loginObject.getInt("BoardMemberID")
                                boardMemberName = loginObject.getString("BoardMembersName")
                                boardMembers = loginObject.getString("BoardMembers")
                                boardMemberRole = loginObject.getString("Role")
                                boardMembersList.add(BoardMembersModel(boardMemberId, boardMemberName,boardMembers, boardMemberRole))
                            }
                            binding!!.btnContainer.visibility = View.VISIBLE
                            boardMembersAdapter = BoardMembersAdapter(this, boardMembersList,this)
                            binding!!.recyclerView.adapter = boardMembersAdapter
                            boardMembersAdapter!!.notifyDataSetChanged()
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                        binding!!.btnContainer.visibility = View.GONE
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onDeleteItemClicked(position: Int) {
        if (employeeIdDeleteList.contains(boardMembersList[position].boardMemberId))
        {
            employeeIdDeleteList.remove(boardMembersList[position].boardMemberId)
        }
        else
        {
            employeeIdDeleteList.add(boardMembersList[position].boardMemberId!!)
        }
    }

    override fun onProgrammerItemClicked(position: Int) {
        if (employeeIdList.contains(availableBoardMembersList[position].availableEmployeeId))
        {
            employeeIdList.remove(availableBoardMembersList[position].availableEmployeeId)
        }
        else
        {
            employeeIdList.add(availableBoardMembersList[position].availableEmployeeId)
        }
    }

    private fun deleteBoardMembers(deleteMembers: ArrayList<Int>) {

        val boardDeleteMembers = deleteMembers.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteBoardMembers + boardDeleteMembers,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                errorMsg = loginObject.getString("errorMsg")
                            }
                            if (errorMsg.equals("Deleted Successfully") || errorMsg == "Deleted Successfully") {
                                getBoardMembers()
                                employeeIdDeleteList.clear()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun getBoardAvailableProgrammers(view: View)
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        availableBoardMembersList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getAvailableBoardProjectMembers + projectId + "&CompanyID=" + SharedPref.getCompanyId(this) + "&BoardID=" + boardId,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                availableEmployeeName = loginObject.getString("EmpName")
                                availableEmployeeId = loginObject.getInt("EmpID")
                                availableBoardMembersList.add(AvailableBoardMembersModel(availableEmployeeName, availableEmployeeId))
                            }
                            availableBoardMembersAdapter = AvailableBoardMembersAdapter(this, availableBoardMembersList,this)
                            popUpLayout(view)
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun popUpLayout(view: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.available_resources_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpRecyclerView=popupView.findViewById(R.id.programmerRecyclerView) as RecyclerView
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddEmployee) as Button
        popUpRecyclerView.adapter = availableBoardMembersAdapter
        availableBoardMembersAdapter!!.notifyDataSetChanged()

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }

        popUpSaveBtn.setOnClickListener()
        {
            if(employeeIdList.isEmpty() || employeeIdList==null)
            {
                CommonMethod.showToast(this, "Please Select the Programmers..!")
            }
            else
            {
                popupWindow.dismiss()
                addBoardMembers(employeeIdList)
            }
        }
    }

    private fun addBoardMembers(members: ArrayList<Int>)
    {
        val projectMembers = members.joinToString(",")

        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.addAvailableBoardMembers + projectMembers + "&CompanyID=" + SharedPref.getCompanyId(this) + "&BoardID=" + boardId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                errMsg = loginObject.getString("errorMsg")
                            }
                            if (errMsg.equals("Board Member Added Successfully") || errMsg == "Board Member Added Successfully") {
                                CommonMethod.showToast(this, "Added Successfully..!")
                                employeeIdList.clear()
                                getBoardMembers()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}