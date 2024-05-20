package com.androidx.helpdesk.timeSheet.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityTimeSheetDetailsBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class TimeSheetDetails : AppCompatActivity() {

    private var binding: ActivityTimeSheetDetailsBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var projectTypeId = 0

    private var moduleId = 0

    private var taskCategoryId = 0

    private var employeeId = 0

    private var taskIntentId = 0

    private var projectDetailsId = 0

    private var moduleDetailsId = 0

    private var taskCategoryDetailsId = 0

    private var allottedToDetails = 0

    private var taskDetailsId = 0

    private var taskStatusDetailsId = 0

    private var taskStatusId = 0

    private var taskStatusTypeId = 0

    private var taskTypeTaskId = 0

    private var taskTypeId = 0

    private var toolsId = 0

    private var toolsTypeId = 0

    private var tasksId = 0

    private var projectName: String? = null

    private var moduleDescription: String? = null

    private var taskCategoryDescription: String? = null

    private var employeeName: String? = null

    private var taskName: String? = null

    private var allottedHours: String? = null

    private var taskDateDetails: String? = null

    private var notesDetails: String? = null

    private var kbNotesDetails: String? = null

    private var taskStatusName: String? = null

    private var taskTypeDescription: String? = null

    private var toolsName: String? = null

    private var errorMsg: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    var moduleIdList: MutableList<Int> = ArrayList()

    var moduleDescriptionList: MutableList<String?> = ArrayList()

    var taskCategoryIdList: MutableList<Int> = ArrayList()

    var taskCategoryDescriptionList: MutableList<String?> = ArrayList()

    var employeeIdList: MutableList<Int> = ArrayList()

    var employeeNameList: MutableList<String?> = ArrayList()

    var taskStatusDescriptionList: MutableList<String?> = ArrayList()

    var taskStatusIdList: MutableList<Int> = ArrayList()

    var taskTypeIdList: MutableList<Int> = ArrayList()

    var taskTypeDescriptionList: MutableList<String?> = ArrayList()

    private val toolsIdList: MutableList<Int> = ArrayList()

    private val toolsNameList: MutableList<String?> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null

    private var moduleNameType: ArrayAdapter<*>? = null

    private var taskCategoryDescriptionType: ArrayAdapter<*>? = null

    private var allottedToType: ArrayAdapter<*>? = null

    private var taskStatusType: ArrayAdapter<*>? = null

    private var taskTypeDescriptionType: ArrayAdapter<*>? = null

    private var toolsType: ArrayAdapter<*>? = null

    private val MY_REQUEST_CODE_PERMISSION = 1000

    private val MY_RESULT_CODE_FILECHOOSER = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_sheet_details)
        moduleNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleDescriptionList)
        taskTypeDescriptionType = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskTypeDescriptionList)
        binding!!.cardView.visibility = View.VISIBLE
        projectList()
        taskCategoryList()
        taskStatus()
        getToolsList()
        initListener()

        binding!!.detailsCommentEt.setOnTouchListener(OnTouchListener { v, event ->
            if (binding!!.detailsCommentEt.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        binding!!.kbNotesEt.setOnTouchListener(OnTouchListener { v, event ->
            if (binding!!.kbNotesEt.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        binding!!.projectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                    if (parentView.getItemAtPosition(position) == "Select") {
                        moduleDescriptionList.clear()
                        moduleIdList.clear()
                        moduleDescriptionList.add("Select")
                        moduleNameType!!.notifyDataSetChanged()
                    } else {
                        projectTypeId = projectIdList[position]
                        getModuleType(projectTypeId)
                        getAssignedToList(projectTypeId)
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.toolsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    toolsTypeId = toolsIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.tasksSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    tasksId = taskTypeIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.taskStatusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    taskStatusTypeId = taskStatusIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.taskCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    if (parentView!!.getItemAtPosition(position) == "Select") {
                        taskTypeDescriptionList.clear()
                        taskTypeIdList.clear()
                        taskTypeDescriptionList.add("Select")
                        taskTypeDescriptionType!!.notifyDataSetChanged()
                    } else {
                        taskTypeId = taskCategoryIdList[position]
                        getTaskType(taskTypeId)
                    }
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
    }

    private fun bundleData()
    {
        val intent = intent
        taskIntentId = intent.getIntExtra("TaskId", 0)
        taskName = intent.getStringExtra("TaskName")
        getTimeSheetTaskDetails(taskIntentId)
    }

    private fun projectList() {
        projectIdList.clear()
        projectNameList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.projectListByUser + SharedPref.getCompanyId(this) + "&EmpID=" + SharedPref.getEmployeeId(this),
            Response.Listener { ServerResponse ->
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
                            setAdapter(1)
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getModuleType(id: Int) {
        moduleDescriptionList.clear()
        moduleIdList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.moduleList + id + "&CompanyID=" + SharedPref.getCompanyId(this),
            Response.Listener { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        moduleDescriptionList.add("Select")
                        moduleIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            moduleId = loginObject.getInt("ModuleID")
                            moduleDescription = loginObject.getString("ModuleName")
                            moduleIdList.add(moduleId)
                            moduleDescriptionList.add(moduleDescription)
                            setAdapter(2)
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun taskCategoryList() {
        taskCategoryIdList.clear()
        taskCategoryDescriptionList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.taskCategoryList + SharedPref.getCompanyId(this),
            Response.Listener { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskCategoryDescriptionList.add("Select")
                        taskCategoryIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskCategoryId = loginObject.getInt("TaskCategoryID")
                            taskCategoryDescription = loginObject.getString("Description")
                            taskCategoryIdList.add(taskCategoryId)
                            taskCategoryDescriptionList.add(taskCategoryDescription)
                            setAdapter(3)
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getAssignedToList(pid: Int) {
        employeeIdList.clear()
        employeeNameList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.assignedTo + SharedPref.getCompanyId(this) + "&ProjectID=" + pid,
            Response.Listener { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        employeeNameList.add("Select")
                        employeeIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            employeeId = loginObject.getInt("EmpID")
                            employeeName = loginObject.getString("EName")
                            employeeIdList.add(employeeId)
                            employeeNameList.add(employeeName)
                            setAdapter(6)
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun taskStatus() {
        taskStatusIdList.clear()
        taskStatusDescriptionList.clear()
        stringRequest = object : StringRequest(
            Method.POST, Api.getTaskStatus,
            Response.Listener { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskStatusDescriptionList.add("Select")
                        taskStatusIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskStatusId = loginObject.getInt("TaskStatusID")
                            taskStatusName = loginObject.getString("TaskStatusDescription")
                            taskStatusIdList.add(taskStatusId)
                            taskStatusDescriptionList.add(taskStatusName)
                            setAdapter(5)
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getTimeSheetTaskDetails(id: Int) {
        stringRequest = object : StringRequest(Method.GET, Api.getTimeSheetDetails + id, Response.Listener
        { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            projectDetailsId = loginObject.getInt("ProjectID")
                            moduleDetailsId = loginObject.getInt("ModuleID")
                            taskDetailsId = loginObject.getInt("TaskID")
                            taskStatusDetailsId = loginObject.getInt("TaskStausID")
                            taskCategoryDetailsId = loginObject.getInt("TaskCategory")
                            allottedToDetails = loginObject.getInt("EmpID")
                            allottedHours = loginObject.getString("AllotedHrs")
                            taskDateDetails = loginObject.getString("TaskDate")
                            notesDetails = loginObject.getString("Notes")
                            kbNotesDetails = loginObject.getString("KBNotes")
                        }
                        binding!!.screenNameEt.setText(taskName)
                        binding!!.hoursAllottedEt.setText(allottedHours)
                        val separatedTaskDateDetails: Array<String> = taskDateDetails!!.split("T").toTypedArray()
                        val correctDateFormat: Array<String> = separatedTaskDateDetails[0].split("-").toTypedArray()
                        binding!!.workDateEt.setText(correctDateFormat[1]+"-"+correctDateFormat[2]+"-"+correctDateFormat[0])
                        binding!!.detailsCommentEt.setText(notesDetails)
                        if(kbNotesDetails!="null")
                        {
                            binding!!.kbNotesEt.setText(kbNotesDetails)
                        }
                        getSpinnerIndex(binding!!.projectSpinner, projectIdList, projectDetailsId, true)
                        getSpinnerIndex(binding!!.taskCategorySpinner, taskCategoryIdList, taskCategoryDetailsId, true)
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            getSpinnerIndex(binding!!.moduleSpinner, moduleIdList, moduleDetailsId, true)
                            getSpinnerIndex(binding!!.tasksSpinner,taskTypeIdList,taskDetailsId,true);
                            getSpinnerIndex(binding!!.allottedToSpinner, employeeIdList, allottedToDetails, true)
                        }, 800)

                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getTaskType(id: Int) {
        taskTypeDescriptionList.clear()
        taskTypeIdList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.getTask + id, Response.Listener
        { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskTypeDescriptionList.add("Select")
                        taskTypeIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskTypeTaskId = loginObject.getInt("TaskID")
                            taskTypeDescription = loginObject.getString("TaskDescription")
                            taskTypeIdList.add(taskTypeTaskId)
                            taskTypeDescriptionList.add(taskTypeDescription)
                            setAdapter(4)
                        }
                    } else {
                        CommonMethod.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return java.util.HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getToolsList() {
        toolsIdList.clear()
        toolsNameList.clear()
        stringRequest = object : StringRequest(Method.POST, Api.getToolsList+SharedPref.getCompanyId(this)+"&EmpID=" + SharedPref.getEmployeeId(this), Response.Listener
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        toolsNameList.add("Select")
                        toolsIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            toolsId = loginObject.getInt("TechID")
                            toolsName = loginObject.getString("Technology")
                            toolsIdList.add(toolsId)
                            toolsNameList.add(toolsName)
                            setAdapter(7)
                        }
                    } else {
                        CommonMethod.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return java.util.HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun getSpinnerIndex(requiredSpinner: Spinner, spinnerList: List<Int>, mySetId: Int, action: Boolean)
    {
        var index = 0
        var i = 0
        while (i < spinnerList.size) {
            if (spinnerList[i] == mySetId) {
                index = i
                i = spinnerList.size
            }
            i++
        }
        requiredSpinner.setSelection(index)
        if (action) {
            requiredSpinner.isEnabled = false
        }
    }

    fun setAdapter(id: Int) {
        if (id == 1)
        {
            projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
            projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.projectSpinner.adapter = projectNameType
        }
        else if (id == 2)
        {
            moduleNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.moduleSpinner.adapter = moduleNameType
        }
        else if (id == 3)
        {
            taskCategoryDescriptionType = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskCategoryDescriptionList)
            taskCategoryDescriptionType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.taskCategorySpinner.adapter = taskCategoryDescriptionType
        }
        else if (id == 4)
        {
            taskTypeDescriptionType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.tasksSpinner.adapter = taskTypeDescriptionType
        }
        else if (id == 5)
        {
            taskStatusType = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskStatusDescriptionList)
            taskStatusType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.taskStatusSpinner.adapter = taskStatusType
        }
        else if (id == 6)
        {
            allottedToType = ArrayAdapter(this, android.R.layout.simple_spinner_item, employeeNameList)
            allottedToType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.allottedToSpinner.adapter = allottedToType
        }
        else if(id == 7)
        {
            toolsType = ArrayAdapter(this, android.R.layout.simple_spinner_item, toolsNameList);
            toolsType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding!!.toolsSpinner.setAdapter(toolsType);
            bundleData()
        }
    }

    private fun initListener() {
        binding!!.cancel.setOnClickListener(onClickListener)
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.chooseFile.setOnClickListener(onClickListener)
        binding!!.submit.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.cancel -> CommonMethod.Companion.showAlertDialog(this, "", " Are you sure you want to cancel?", "Yes", "No",
                object : CommonMethod.DialogClickListener {
                    override fun dialogOkBtnClicked(value: String?) {
                        finish()
                    }
                    override fun dialogNoBtnClicked(value: String?) {}
                }
            )
            R.id.backButton -> finish()
            R.id.chooseFile ->doBrowseFile()
            R.id.submit -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this)) {
                    updateTimeSheetDetails()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean {

        if (toolsTypeId == 0) {
            CommonMethod.showToast(this, "Select Task Working Tools")
            binding!!.toolsSpinner.requestFocus()
            return false
        }
        else if (binding!!.hoursSpentEt.text.toString() == null || binding!!.hoursSpentEt.text.toString().isEmpty())
        {
            binding!!.hoursSpentEt.error = "Enter Total Hours"
            binding!!.hoursSpentEt.requestFocus()
            return false
        }
        else if (binding!!.hoursSpentEt.text.toString() == "0")
        {
            binding!!.hoursSpentEt.error = "Enter Valid Hours"
            binding!!.hoursSpentEt.requestFocus()
            return false
        }
        else if (taskStatusTypeId == 0) {
            CommonMethod.showToast(this, "Select Task Status")
            binding!!.taskStatusSpinner.requestFocus()
            return false
        }
        else if (binding!!.detailsCommentEt.text.toString() == null || binding!!.detailsCommentEt.text.toString().isEmpty()) {
            binding!!.detailsCommentEt.error = "Enter Task Description"
            binding!!.detailsCommentEt.requestFocus()
            return false
        }
        else
        {
            return true
        }
    }



    private fun doBrowseFile() {
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data

            var filePath: String? = null
            var file: File?
            try {
                file = File(selectedFile!!.getPath())
                filePath = file.path
//                Log.d("Error", filePath.toString())
                filePath = file.absolutePath
//                Log.d("Error", filePath.toString())
                filePath = file.canonicalPath
//                Log.d("Error", filePath.toString())
                filePath = file.name
//                Log.d("Error", filePath.toString())
            } catch (e: Exception) {
                CommonMethod.showToast(this, e.toString())
            }
            binding!!.fileNames.text = filePath
        }
    }

    fun updateTimeSheetDetails()
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = object : StringRequest(Method.GET, Api.updateTimeSheetDetails +taskIntentId+"&TaskName=" +binding!!.screenNameEt.text.toString()+"&Tools="+toolsTypeId+"&HoursSpend="+binding!!.hoursSpentEt.text.toString()+"&TaskStatus="+taskStatusTypeId+"&DocsCount="+binding!!.documentsCountEt.text.toString()+"&Billable="+binding!!.billableCb.isChecked+"&Comments="+binding!!.detailsCommentEt.text.toString()+"&KBNotes="+binding!!.kbNotesEt.text.toString(),
            Response.Listener { ServerResponse ->
                binding!!.cardView.visibility = View.VISIBLE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONObject("data")
                        errorMsg = dataArray.getString("Error Msg")
                        CommonMethod.showToast(this, errorMsg)
                        finish()
                    } else {
                        CommonMethod.showToast(this, " Wrong")
                    }
                } catch (e: JSONException) {
                    CommonMethod.showToast(this, "Something Went Wrong")
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
             {
                binding!!.cardView.visibility = View.GONE
                stringRequest!!.retryPolicy = DefaultRetryPolicy(100,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                CommonMethod.showToast(this, "Error")
            }
        ) {
            override fun getParams(): Map<String, String>? {
                return java.util.HashMap()
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

}