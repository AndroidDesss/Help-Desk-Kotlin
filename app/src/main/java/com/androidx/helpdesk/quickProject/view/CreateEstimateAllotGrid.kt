package com.androidx.helpdesk.quickProject.view

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
import com.androidx.helpdesk.authentication.DashBoardActivity
import com.androidx.helpdesk.databinding.ActivityCreateEstimateAllotGridBinding
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.quickProject.adapter.GridListAdapter
import com.androidx.helpdesk.quickProject.edit.EditEstimateAllotGrid
import com.androidx.helpdesk.quickProject.model.GridListModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class CreateEstimateAllotGrid : AppCompatActivity(), OnProgrammerListener {

    private var binding: ActivityCreateEstimateAllotGridBinding? = null

    private var stringRequest: StringRequest? = null

    private var projectId = 0

    private var moduleId = 0

    private var subModuleId = 0

    private var status = 0

    private var gridProjectTaskAllotId = 0

    private var gridProjectId = 0

    private var gridModuleId = 0

    private var gridProjectTaskId = 0

    private var gridSequence = 0

    private var newProjectId = 0

    private var taskCategoryId = 0

    private var gridTaskHours: Double = 0.0

    private var gridAssignedBy: String? = null

    private var gridAssignedTo: String? = null

    private var gridTaskType: String? = null

    private var projectName: String? = null

    private var gridStartDate: String? = null

    private var gridEndDate: String? = null

    private var gridTaskDate: String? = null

    private var gridTaskStatus: String? = null

    private var gridTaskName: String? = null

    private var errorMsg: String? = null

    private var errMsg: String? = null

    private var gridList: MutableList<GridListModel> = ArrayList()

    private var gridListAdapter: GridListAdapter? = null

    private var newAllottedList = ArrayList<Int>()

    private var newModuleList = ArrayList<Int>()

    private var newSubModuleList = ArrayList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_estimate_allot_grid)
        getBundleData()
        initListener()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.btnDuplicate.setOnClickListener(onClickListener)
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        moduleId = mIntent.getIntExtra("moduleId", 0)
        subModuleId = mIntent.getIntExtra("subModuleId", 0)
        taskCategoryId = mIntent.getIntExtra("taskCategoryId", 0)
        getGridListValues()
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnNext ->
            {
                if(newAllottedList.isEmpty() || newAllottedList==null)
                {
                    CommonMethod.showToast(this, "Please Select the Task..!")
                }
                else
                {
                    pushToTaskAllotment(newAllottedList,newProjectId,newModuleList,newSubModuleList)
                }
            }
            R.id.btnDuplicate ->
            {
                if(newAllottedList.isEmpty() || newAllottedList==null)
                {
                    CommonMethod.showToast(this, "Please Select the Task..!")
                }
                else
                {
                    taskDuplicate(newAllottedList)
                }

            }
        }
    }

    private fun gridListEdit(pid: Int,mid: Int,ptId: Int,ptaId: Int) {
        startActivity(Intent(this, EditEstimateAllotGrid::class.java).putExtra("projectId",pid).putExtra("ModuleId",mid).putExtra("projectTaskId",ptId).putExtra("projectTaskAllotId",ptaId).putExtra("taskCategoryId",taskCategoryId))
    }

    private fun getGridListValues()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        gridList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getGridListValues + projectId + "&ModuleID=" + moduleId + "&PrjTaskID=" + subModuleId,
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
                                gridProjectTaskAllotId = loginObject.getInt("PrjTaskAllotID")
                                gridProjectId = loginObject.getInt("ProjectID")
                                gridModuleId = loginObject.getInt("ModuleID")
                                gridProjectTaskId = loginObject.getInt("PrjTaskID")
                                gridStartDate = loginObject.getString("EstStartDate")
                                gridEndDate = loginObject.getString("EstEndDate")
                                gridTaskDate = loginObject.getString("TaskDate")
                                gridTaskStatus = loginObject.getString("TaskStatus")
                                gridTaskName = loginObject.getString("Task")
                                gridSequence = loginObject.getInt("Sequence")
                                gridAssignedBy = loginObject.getString("AssignedByName")
                                gridAssignedTo = loginObject.getString("AssignedtoName")
                                gridTaskType = loginObject.getString("TaskTypeName")
                                gridTaskHours = loginObject.getDouble("ActualHours")
                                gridList.add(GridListModel(gridProjectTaskAllotId,gridProjectId,gridModuleId,gridProjectTaskId,gridStartDate,gridEndDate,gridTaskDate,gridTaskStatus,gridTaskName,gridSequence,gridAssignedBy,gridAssignedTo,gridTaskType,gridTaskHours))
                            }
                            binding!!.btnContainer.visibility = View.VISIBLE
                            gridListAdapter = GridListAdapter(this, gridList,this)
                            binding!!.recyclerView.adapter = gridListAdapter
                            gridListAdapter!!.notifyDataSetChanged()
                            gridListAdapter!!.setOnClickListener(object :
                                GridListAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, adapterProjectId: Int, adapterModuleId: Int, adapterProjectTaskId: Int, adapterProjectTaskAllotId: Int) {
                                    if(holder == "delete")
                                    {
                                        gridListDelete(adapterProjectTaskAllotId,adapterProjectId,adapterModuleId,adapterProjectTaskId)
                                    }
                                    else
                                    {
                                        gridListEdit(adapterProjectId,adapterModuleId,adapterProjectTaskId,adapterProjectTaskAllotId)
                                    }
                                }
                            })
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.btnContainer.visibility = View.GONE
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
        if (newAllottedList.contains(gridList[position].gridProjectTaskAllotId))
        {
            newAllottedList.remove(gridList[position].gridProjectTaskAllotId)
            newModuleList.remove(gridList[position].gridModuleId)
            newSubModuleList.remove(gridList[position].gridProjectTaskId)
        }
        else
        {
            newAllottedList.add(gridList[position].gridProjectTaskAllotId)
            newModuleList.add(gridList[position].gridModuleId)
            newSubModuleList.add(gridList[position].gridProjectTaskId)
            newProjectId = gridList[position].gridProjectId
        }
    }

    private fun gridListDelete(ptaId: Int,pId: Int,mId: Int,smId: Int) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteEstimateAllotGrid + ptaId + "&ProjectID=" + pId + "&ModuleID=" + mId + "&SubModuleID=" + smId,
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
                                newAllottedList.clear()
                                newModuleList.clear()
                                newSubModuleList.clear()
                                getGridListValues()
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

    private fun pushToTaskAllotment(allotList: ArrayList<Int>,pjId: Int,moduleList: ArrayList<Int>,subModuleList: ArrayList<Int>)
    {
        val prjAtId = allotList.joinToString(",")
        val mId = moduleList.joinToString(",")
        val smId = subModuleList.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.pushToTaskAllotment + prjAtId + "&ProjectID=" + pjId + "&UserName=" + SharedPref.getCompanyId(this)+"&ModuleID=" + mId + "&SubModuleID=" + smId,
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
                            if (errMsg.equals("Task Pushed Successfully") || errMsg == "Task Pushed Successfully") {
                                newAllottedList.clear()
                                newModuleList.clear()
                                newSubModuleList.clear()
                                CommonMethod.showToast(this, "Task Pushed Successfully..!")
                                startActivity(Intent(this, DashBoardActivity::class.java))
                                finish()
                            }
                        }
                    }
                    else
                    {
                        CommonMethod.showToast(this, "Please Assign Task to Employee..!")
                        binding!!.cardView.visibility = View.GONE
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

    private fun taskDuplicate(allotList: ArrayList<Int>)
    {
        val prjAtId = allotList.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.taskDuplicate + prjAtId ,
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
                            if (errMsg.equals("Duplicated Successfully") || errMsg == "Duplicated Successfully") {
                                newAllottedList.clear()
                                newModuleList.clear()
                                newSubModuleList.clear()
                                getGridListValues()
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

    override fun onRestart() {
        super.onRestart()
        getGridListValues()
    }

}