package com.androidx.helpdesk.editQuickProject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityQuickEditEstimateBinding
import com.androidx.helpdesk.editQuickProject.adapter.EditTaskListAdapter
import com.androidx.helpdesk.editQuickProject.model.EditTaskListModel
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class QuickEditEstimate : AppCompatActivity(), OnProgrammerListener {

    private var binding: ActivityQuickEditEstimateBinding? = null

    private var stringRequest: StringRequest? = null

    private var projectId = 0

    private var status = 0

    private var spinnerModuleId = 0

    private var moduleId = 0

    private var projectTaskId = 0

    private var prjTaskId = 0

    private var taskCategoryId = 0

    private var taskResponseId = 0

    private var taskResponseCategoryId = 0

    private var moduleName: String? = null

    private var projectName: String? = null

    private var projectTaskName: String? = null

    private var taskResponseName: String? = null

    private var errMsg: String? = null

    private val moduleIdList: MutableList<Int> = ArrayList()

    private val moduleNameList: MutableList<String?> = ArrayList()

    private val projectTaskIdList: MutableList<Int> = ArrayList()

    private val projectTaskNameList: MutableList<String?> = ArrayList()

    private var moduleAdapter: ArrayAdapter<*>? = null

    private var screenNameAdapter: ArrayAdapter<*>? = null

    private var editTaskList: MutableList<EditTaskListModel> = ArrayList()

    private var editTaskListAdapter: EditTaskListAdapter? = null

    private var taskIdList = ArrayList<Int>()

    private var taskCategorySpinnerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quick_edit_estimate)
        getBundleData()
        initListener()

        binding!!.moduleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                    if (parentView.getItemAtPosition(position) == "Select") {
                    } else {
                        binding!!.cardView.visibility = View.VISIBLE
                        spinnerModuleId = moduleIdList[position]
                        getScreenNameList(spinnerModuleId)
                    }
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.btnSearch.setOnClickListener(onClickListener)
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        getModuleDropDownList()
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
                    addTask(taskIdList,taskCategorySpinnerId,spinnerModuleId,prjTaskId)
                }
            }
            R.id.btnSearch ->
            {
                if(spinnerModuleId == 0 || spinnerModuleId.equals(0))
                {
                    CommonMethod.showToast(this, "Select Module")
                }
                else if(binding!!.screenNameSpinner.selectedItem.toString() == "Select" || binding!!.screenNameSpinner.selectedItem.toString().equals("Select"))
                {
                    CommonMethod.showToast(this, "Select ScreenName")
                }
                else
                {
                    searchProjectTaskId(spinnerModuleId,binding!!.screenNameSpinner.selectedItem.toString(),projectId)
                }
            }
        }
    }

    private fun getModuleDropDownList()
    {
        moduleIdList.clear()
        moduleNameList.clear()
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.GET, Api.getEstimateModule + SharedPref.getCompanyId(this)+"&ProjectID="+projectId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        moduleNameList.add("Select")
                        moduleIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            moduleId = loginObject.getInt("ModuleID")
                            moduleName = loginObject.getString("ModuleName")
                            moduleIdList.add(moduleId)
                            moduleNameList.add(moduleName)
                        }
                        binding!!.cardView.visibility = View.GONE
                        setAdapter(1)
                    } else {
                        CommonMethod.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    private fun getScreenNameList(moduleId: Int)
    {
        projectTaskIdList.clear()
        projectTaskNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getEstimateScreenName + SharedPref.getCompanyId(this) + "&ModuleID=" + moduleId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        projectTaskNameList.add("Select")
                        projectTaskIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            projectTaskId = loginObject.getInt("PrjTaskID")
                            projectTaskName = loginObject.getString("TaskName")
                            projectTaskIdList.add(projectTaskId)
                            projectTaskNameList.add(projectTaskName)
                        }
                        binding!!.cardView.visibility = View.GONE
                        setAdapter(2)
                    } else {
                        binding!!.cardView.visibility = View.GONE
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun setAdapter(id: Int) {
        if (id == 1) {
            moduleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.moduleSpinner.adapter = moduleAdapter
        } else if (id == 2) {
            screenNameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectTaskNameList)
            screenNameAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.screenNameSpinner.adapter = screenNameAdapter
            binding!!.btnSearch.visibility = View.VISIBLE
        }
    }

    private fun searchProjectTaskId(mId: Int,sName: String,pId: Int)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.GET, Api.getEstimateAllotProjectTaskId + mId + "&screenname=" + sName + "&ProjectID=" + pId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                prjTaskId = loginObject.getInt("PrjTaskID")
                                taskCategoryId = loginObject.getInt("TaskCategory")
                            }
                            searchTaskList(taskCategoryId,pId,mId,prjTaskId)
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

    private fun searchTaskList(taskCategoryId: Int,pId: Int,mId: Int,prjId: Int)
    {
        binding!!.rlError.visibility = View.GONE
        editTaskList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getEstimateAllotTaskList + taskCategoryId + "&ProjectID=" + pId + "&ModuleID=" + mId + "&PrjTaskID=" + prjId,
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
                                taskResponseCategoryId = loginObject.getInt("TaskCategory")
                                editTaskList.add(EditTaskListModel(taskResponseId, taskResponseName,taskResponseCategoryId))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            editTaskListAdapter = EditTaskListAdapter(this, editTaskList,this)
                            binding!!.recyclerView.adapter = editTaskListAdapter
                            editTaskListAdapter!!.notifyDataSetChanged()
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
        if (taskIdList.contains(editTaskList[position].taskResponseId))
        {
            taskIdList.remove(editTaskList[position].taskResponseId)
        }
        else
        {
            taskIdList.add(editTaskList[position].taskResponseId)
            taskCategorySpinnerId = editTaskList[position].taskResponseCategoryId
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
}