package com.androidx.helpdesk.backLog.view

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
import com.androidx.helpdesk.databinding.ActivityNewBackLogScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class NewBackLogScreen : AppCompatActivity() {

    private var binding: ActivityNewBackLogScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var issueId = 0

    private var workFlowId = 0

    private var selectedProjectId = 0

    private var reportedById = 0

    private var moduleId= 0

    private var boardId= 0

    private var sprintId= 0

    private var projectName: String? = null

    private var issueName: String? = null

    private var workFlowName: String? = null

    private var reportedByName: String? = null

    private var moduleName: String? = null

    private var boardName: String? = null

    private var sprintName: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private val issueIdList: MutableList<Int> = ArrayList()

    private val issueNameList: MutableList<String?> = ArrayList()

    private val workFlowIdList: MutableList<Int> = ArrayList()

    private val workFlowNameList: MutableList<String?> = ArrayList()

    private val reportedByIdList: MutableList<Int> = ArrayList()

    private val reportedByNameList: MutableList<String?> = ArrayList()

    private val moduleIdList: MutableList<Int> = ArrayList()

    private val moduleNameList: MutableList<String?> = ArrayList()

    private val boardIdList: MutableList<Int> = ArrayList()

    private val boardNameList: MutableList<String?> = ArrayList()

    private val sprintIdList: MutableList<Int> = ArrayList()

    private val sprintNameList: MutableList<String?> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null

    private var issueNameType: ArrayAdapter<*>? = null

    private var workFlowType: ArrayAdapter<*>? = null

    private var reportedByType: ArrayAdapter<*>? = null

    private var moduleType: ArrayAdapter<*>? = null

    private var boardType: ArrayAdapter<*>? = null

    private var sprintType: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_back_log_screen)
        binding!!.cardView.visibility = View.VISIBLE
        projectList()

        binding!!.projectNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedProjectId = projectIdList[position]
//                    getEmail(selectedProjectId)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun projectList()
    {
        projectIdList.clear()
        projectNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogProjectList + SharedPref.getCompanyId(this)  + "&UserTypeID=" + SharedPref.getUserId(this) + "&Est&EmpID=" + SharedPref.getEmployeeId(this),
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        projectNameList.add("Select")
                        projectIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            projectId = loginObject.getInt("ProjectID")
                            projectName = loginObject.getString("ProjectName")
                            projectIdList.add(projectId)
                            projectNameList.add(projectName)
                        }
                        setAdapter(1)
                    } else {
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

    private fun getIssueType()
    {
        issueIdList.clear()
        issueNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogIssueType ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        issueNameList.add("Select")
                        issueIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            issueId = loginObject.getInt("ModuleTypeID")
                            issueName = loginObject.getString("ModuleTypeName")
                            issueIdList.add(projectId)
                            issueNameList.add(projectName)
                        }
                        setAdapter(2)
                    } else {
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

    private fun getWorkFlowType()
    {
        workFlowIdList.clear()
        workFlowNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogWorkFlow + SharedPref.getCompanyId(this) ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        workFlowNameList.add("Select")
                        workFlowIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            workFlowId = loginObject.getInt("TaskCategoryID")
                            workFlowName = loginObject.getString("Description")
                            workFlowIdList.add(workFlowId)
                            workFlowNameList.add(workFlowName)
                        }
                        setAdapter(3)
                    } else {
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

    private fun getReportedByType()
    {
        reportedByIdList.clear()
        reportedByNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogReportedType + SharedPref.getCompanyId(this) ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        reportedByNameList.add("Select")
                        reportedByIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            reportedById = loginObject.getInt("EmpID")
                            reportedByName = loginObject.getString("EmpName")
                            reportedByIdList.add(workFlowId)
                            reportedByNameList.add(workFlowName)
                        }
                        setAdapter(4)
                    } else {
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

    private fun getModuleType(projectIdSelected: Int)
    {
        moduleIdList.clear()
        moduleNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogModuleType + SharedPref.getCompanyId(this) + "&ProjectID=" + projectIdSelected,
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
                        setAdapter(5)
                    } else {
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

    private fun getBoardType(projectIdSelected: Int)
    {
        boardIdList.clear()
        boardNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogBoardType + projectIdSelected ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        boardNameList.add("Select")
                        boardIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            boardId = loginObject.getInt("BoardID")
                            boardName = loginObject.getString("BoardName")
                            boardIdList.add(boardId)
                            boardNameList.add(boardName)
                        }
                        setAdapter(6)
                    } else {
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

    private fun getSprintType(projectIdSelected: Int)
    {
        sprintIdList.clear()
        sprintNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogSprintType + projectIdSelected ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        sprintNameList.add("Select")
                        sprintIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            sprintId = loginObject.getInt("SprintID")
                            sprintName = loginObject.getString("SprintName")
                            sprintIdList.add(sprintId)
                            sprintNameList.add(sprintName)
                        }
                        setAdapter(7)
                    } else {
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

    fun setAdapter(id: Int)
    {
        if (id == 1)
        {
            projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
            projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.projectNameSpinner.adapter = projectNameType
        }
        else if (id == 2)
        {
            issueNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, issueNameList)
            issueNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.issueTypeSpinner.adapter = issueNameType
        }
        else if (id == 3)
        {
            workFlowType = ArrayAdapter(this, android.R.layout.simple_spinner_item, workFlowNameList)
            workFlowType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.workFlowSpinner.adapter = workFlowType
        }
        else if (id == 4)
        {
            reportedByType = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportedByNameList)
            reportedByType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.reportedBySpinner.adapter = reportedByType
        }
        else if (id == 5)
        {
            moduleType = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.moduleEpicSpinner.adapter = moduleType
        }
        else if (id == 6)
        {
            boardType = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.boardSpinner.adapter = boardType
        }
        else if (id == 7)
        {
            sprintType = ArrayAdapter(this, android.R.layout.simple_spinner_item, sprintNameList)
            sprintType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.sprintSpinner.adapter = sprintType
        }
    }
}