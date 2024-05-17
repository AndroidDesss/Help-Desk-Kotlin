package com.androidx.helpdesk.quickProject.edit

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditEstimateAllotGridBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EditEstimateAllotGrid : AppCompatActivity() {

    private var binding: ActivityEditEstimateAllotGridBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var moduleId = 0

    private var projectTaskId = 0

    private var projectTaskAllotId = 0

    private var boardId = 0

    private var scrumId = 0

    private var taskModuleId = 0

    private var assignedByEmpId = 0

    private var assignedToEmpId = 0

    private var taskStatusId = 0

    private var selectedBoardId = 0

    private var selectedScrumId = 0

    private var selectedAssignedById = 0

    private var selectedAssignedToId = 0

    private var selectedTaskStatusId = 0

    private var selectedTaskTypeId = 0

    private var taskCategoryId = 0

    private var errMsg: String? = null

    private var boardName: String? = null

    private var scrumName: String? = null

    private var taskStatusName: String? = null

    private var assignedByEmpName: String? = null

    private var assignedToEmpName: String? = null

    private var taskModuleName: String? = null

    private var gridAllotTaskDate: String? = null

    private var gridAllotTaskName: String? = null

    private var gridAllotProjectName: String? = null

    private var gridAllotModuleName: String? = null

    private var gridAllotScreenName: String? = null

    private var gridAllotStartDate: String? = null

    private var gridAllotEndDate: String? = null

    private var gridAllotTaskType: String? = null

    private val boardIdList: MutableList<Int> = ArrayList()

    private val boardNameList: MutableList<String?> = ArrayList()

    private val scrumIdList: MutableList<Int> = ArrayList()

    private val scrumNameList: MutableList<String?> = ArrayList()

    private val assignedByEmpIdList: MutableList<Int> = ArrayList()

    private val assignedByEmpNameList: MutableList<String?> = ArrayList()

    private val assignedToEmpIdList: MutableList<Int> = ArrayList()

    private val assignedToEmpNameList: MutableList<String?> = ArrayList()

    private val taskModuleIdList: MutableList<Int> = ArrayList()

    private val taskModuleNameList: MutableList<String?> = ArrayList()

    private val taskStatusIdList: MutableList<Int> = ArrayList()

    private val taskStatusNameList: MutableList<String?> = ArrayList()

    private var boardAdapter: ArrayAdapter<*>? = null

    private var scrumAdapter: ArrayAdapter<*>? = null

    private var assignedByAdapter: ArrayAdapter<*>? = null

    private var assignedToAdapter: ArrayAdapter<*>? = null

    private var taskTypeAdapter: ArrayAdapter<*>? = null

    private var taskStatusAdapter: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_estimate_allot_grid)
        binding!!.cardView.visibility = View.VISIBLE
        getBundleData()
        initListener()

        binding!!.boardSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedBoardId = boardIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.scrumSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedScrumId = scrumIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.assignedBySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedAssignedById = assignedByEmpIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.assignedToSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedAssignedToId = assignedToEmpIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.taskTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedTaskTypeId = taskModuleIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.taskStatusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedTaskStatusId = taskStatusIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        moduleId = mIntent.getIntExtra("ModuleId",0)
        projectTaskId = mIntent.getIntExtra("projectTaskId", 0)
        projectTaskAllotId = mIntent.getIntExtra("projectTaskAllotId",0)
        taskCategoryId = mIntent.getIntExtra("taskCategoryId",0)
        getBoardList()
        getAssignedByList()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
        binding!!.taskDateValue.setOnClickListener(onClickListener)
    }

    private fun getBoardList()
    {
        boardIdList.clear()
        boardNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getBoardListValues  + projectId,
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
                        setAdapter(1,binding!!.boardSpinner)
                        getScrumList()
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

    private fun getScrumList()
    {
        scrumIdList.clear()
        scrumNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getSprintList + projectId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        scrumNameList.add("Select")
                        scrumIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            scrumId = loginObject.getInt("SprintID")
                            scrumName = loginObject.getString("SprintName")
                            scrumIdList.add(scrumId)
                            scrumNameList.add(scrumName)
                        }
                        setAdapter(2,binding!!.scrumSpinner)

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

    private fun getAssignedByList()
    {
        assignedByEmpIdList.clear()
        assignedByEmpNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getAssignedByList + SharedPref.getCompanyId(this) +"&ProjectID=" + projectId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        assignedByEmpNameList.add("Select")
                        assignedByEmpIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            assignedByEmpId = loginObject.getInt("EmpID")
                            assignedByEmpName = loginObject.getString("EmpName")
                            assignedByEmpIdList.add(assignedByEmpId)
                            assignedByEmpNameList.add(assignedByEmpName)
                        }
                        setAdapter(3,binding!!.assignedBySpinner)
                        getAssignedToList()
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

    private fun getAssignedToList()
    {
        assignedToEmpIdList.clear()
        assignedToEmpNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getAssignedToList + SharedPref.getCompanyId(this) +"&ProjectID=" + projectId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        assignedToEmpNameList.add("Select")
                        assignedToEmpIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            assignedToEmpId = loginObject.getInt("EmpID")
                            assignedToEmpName = loginObject.getString("EmpName")
                            assignedToEmpIdList.add(assignedToEmpId)
                            assignedToEmpNameList.add(assignedToEmpName)
                        }
                        setAdapter(4,binding!!.assignedToSpinner)
                        getTaskType()
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

    private fun getTaskType()
    {
        taskModuleIdList.clear()
        taskModuleNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getTaskType ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskModuleNameList.add("Select")
                        taskModuleIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskModuleId = loginObject.getInt("ModuleTypeID")
                            taskModuleName = loginObject.getString("ModuleTypeName")
                            taskModuleIdList.add(taskModuleId)
                            taskModuleNameList.add(taskModuleName)
                        }
                        setAdapter(5,binding!!.taskTypeSpinner)
                        getTaskStatus()
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

    private fun getTaskStatus()
    {
        taskStatusIdList.clear()
        taskStatusNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getEstimateAllotTaskStatus ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskStatusNameList.add("Select")
                        taskStatusIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskStatusId = loginObject.getInt("TaskStatusID")
                            taskStatusName = loginObject.getString("TaskStatusDescription")
                            taskStatusIdList.add(taskStatusId)
                            taskStatusNameList.add(taskStatusName)
                        }
                        setAdapter(6,binding!!.taskStatusSpinner)
                        getGridAllotEditValues(projectId,moduleId,projectTaskId,projectTaskAllotId)
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



    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.taskDateValue ->
            {
                setDate(binding!!.taskDateValue)
            }
            R.id.btnSave ->
            {
                updateGridAllot()
            }
        }
    }

    private fun setDate(editText: EditText)
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                editText.setText(StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth.toString()).append("/").append(year))
            }, year, month, day)
        datePickerDialog.show()
    }

    fun setAdapter(id: Int,spinnerId: Spinner) {
        if (id == 1) {
            boardAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = boardAdapter
        } else if (id == 2) {
            scrumAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, scrumNameList)
            scrumAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = scrumAdapter
        }
        else if (id == 3) {
            assignedByAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, assignedByEmpNameList)
            assignedByAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = assignedByAdapter
        }
        else if (id == 4) {
            assignedToAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, assignedToEmpNameList)
            assignedToAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = assignedToAdapter
        }
        else if (id == 5) {
            taskTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskModuleNameList)
            taskTypeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = taskTypeAdapter
        }
        else if (id == 6) {
            taskStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskStatusNameList)
            taskStatusAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = taskStatusAdapter
        }
    }

    private fun getGridAllotEditValues(pId : Int?, mId : Int?,prjTkId: Int?,prjTkAtId: Int?)
    {
        stringRequest = StringRequest(Request.Method.POST, Api.editGridAllotEdit +  pId + "&ModuleID=" + mId + "&PrjTaskID=" + prjTkId + "&PrjTaskAllotID=" + prjTkAtId,
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
                                gridAllotTaskDate = loginObject.optString("TaskDate")
                                gridAllotTaskName = loginObject.getString("Task")
                                gridAllotProjectName = loginObject.getString("ProjectName")
                                gridAllotModuleName = loginObject.getString("ModuleName")
                                gridAllotScreenName = loginObject.getString("TaskName")
                                gridAllotStartDate = loginObject.optString("EstStartDate")
                                gridAllotEndDate = loginObject.optString("EstEndDate")
                                gridAllotTaskType = loginObject.optString("TaskType")
                            }
                            val separatedTaskDate: Array<String> = gridAllotTaskDate!!.split("T").toTypedArray()
                            binding!!.taskDateValue.setText(separatedTaskDate[0])
                            binding!!.projectValue.setText(gridAllotProjectName)
                            binding!!.moduleValue.setText(gridAllotModuleName)
                            binding!!.screenNameValue.setText(gridAllotScreenName)
                            binding!!.taskValue.setText(gridAllotTaskName)
                            val separatedStartDate: Array<String> = gridAllotStartDate!!.split("T").toTypedArray()
                            val separatedEndDate: Array<String> = gridAllotEndDate!!.split("T").toTypedArray()
                            binding!!.estStartDateValue.setText(separatedStartDate[0])
                            binding!!.estEndDateValue.setText(separatedEndDate[0])
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

    private fun updateGridAllot()
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.updateAllotGrid + projectTaskAllotId + "&CompanyID="+ SharedPref.getCompanyId(this) +"&ProjectID=" + projectId + "&ModuleID=" + moduleId + "&AllotedHrs=" + binding!!.hoursValue.text.toString() +"&DocCount=" + binding!!.docCountValue.text.toString() +"&EmpID=" + SharedPref.getEmployeeId(this) +"&TaskStausID=" + selectedTaskStatusId + "&TaskName=" + binding!!.screenNameValue.text.toString() + "&Assignedto=" + selectedAssignedToId + "&TaskType=" + selectedTaskTypeId + "&Billable=" +  binding!!.isBillable.isChecked + "&Task="+ binding!!.taskValue.text.toString() + "&Notes=" + binding!!.notesValue.text.toString() + "&TaskCategoryID=" + taskCategoryId + "&SubModuleID=" + projectTaskId + "&TaskDate=" + binding!!.taskDateValue.text.toString() + "&UserName=" + SharedPref.getFirstName(this) + "&EstdStartDate=" + binding!!.estStartDateValue.text.toString() + "&EstdEndDate=" + binding!!.estEndDateValue.text.toString() + "&SprintID=" + selectedScrumId+ "&BoardID="+ selectedBoardId,
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
                            if (errMsg.equals("Updated Successfully") || errMsg == "Updated Successfully") {
                                CommonMethod.showToast(this, "Updated Successfully")
                                finish()
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