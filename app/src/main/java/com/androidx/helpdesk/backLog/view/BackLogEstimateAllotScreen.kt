package com.androidx.helpdesk.backLog.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.backLog.adapter.BackLogTaskListAdapter
import com.androidx.helpdesk.backLog.model.BackLogTaskListModel
import com.androidx.helpdesk.databinding.ActivityBackLogEstimateAllotScreenBinding
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.editQuickProject.view.QuickEditEstimateAllotGrid
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class BackLogEstimateAllotScreen : AppCompatActivity(), OnProgrammerListener {

    private var binding: ActivityBackLogEstimateAllotScreenBinding? = null

    private var firstVisit = false

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var moduleId = 0

    private var projectTaskId = 0

    private var taskCategoryId = 0

    private var taskResponseId = 0

    private var taskIdList = ArrayList<Int>()

    private var taskResponseName: String? = null

    private var errMsg: String? = null

    private var projectName: String? = null

    private var taskList: MutableList<BackLogTaskListModel> = ArrayList()

    private var backLogTaskListAdapter: BackLogTaskListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_back_log_estimate_allot_screen)
        firstVisit = true
        getBundleData()
        initListener()
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        moduleId = mIntent.getIntExtra("moduleId", 0)
        projectTaskId = mIntent.getIntExtra("projectTaskId", 0)
        taskCategoryId = mIntent.getIntExtra("taskCategoryId", 0)
        projectName = mIntent.getStringExtra("projectName")
        getTaskList(projectId,moduleId,projectTaskId,taskCategoryId)
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnNext ->
            {
                if(taskIdList.isEmpty() || taskIdList==null )
                {
                    CommonMethod.showToast(this, "Please Select the Task..!")
                }
                else
                {
                    addTask(taskIdList,taskCategoryId,moduleId,projectTaskId)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getTaskList(projectId: Int, moduleId: Int, projectTaskId: Int, taskCategoryId: Int)
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        taskList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getBackLogTaskList + taskCategoryId + "&ProjectID=" + projectId + "&ModuleID=" + moduleId + "&PrjTaskID=" + projectTaskId,
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
                                taskResponseId = loginObject.getInt("TaskID")
                                taskResponseName = loginObject.getString("TaskDescription")
                                taskList.add(BackLogTaskListModel(taskResponseId, taskResponseName))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            backLogTaskListAdapter = BackLogTaskListAdapter(this, taskList,this)
                            binding!!.recyclerView.adapter = backLogTaskListAdapter
                            backLogTaskListAdapter!!.notifyDataSetChanged()
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.btnNext.visibility = View.GONE
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

    override fun onProgrammerItemClicked(position: Int) {
        if (taskIdList.contains(taskList[position].taskResponseId))
        {
            taskIdList.remove(taskList[position].taskResponseId)
        }
        else
        {
            taskIdList.add(taskList[position].taskResponseId!!)
        }
    }

    private fun addTask(tId: ArrayList<Int>,tcId: Int,mId: Int,smId:Int)
    {
        val tIds = tId.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.addTaskEstimateAllot + tIds + "&CompanyID=" + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId + "&ModuleID=" + mId + "&SubModuleID=" +smId+ "&EmpID=" + SharedPref.getEmployeeId(this) + "&TaskCategoryID=" + tcId + "&UserName=" + SharedPref.getCompanyId(this),
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
                                errMsg = loginObject.getString("errorMsg")
                            }
                            if (errMsg.equals("Inserted Successfully") || errMsg == "Inserted Successfully") {
                                taskIdList.clear()
                                CommonMethod.showToast(this, "Added Successfully..!")
                                startActivity(Intent(this, QuickEditEstimateAllotGrid::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName).putExtra("moduleId",mId).putExtra("subModuleId",smId).putExtra("taskCategoryId",taskCategoryId))
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

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        }
        else {
            getTaskList(projectId,moduleId,projectTaskId,taskCategoryId)
        }
    }
}