package com.androidx.helpdesk.quickProject.edit

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.VolleyMultipartRequest
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditSubModuleBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class EditSubModule : AppCompatActivity() {

    private var binding: ActivityEditSubModuleBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectTaskId = 0

    private var projectId = 0

    private var issueId = 0

    private var workFlowId = 0

    private var reportedById = 0

    private var moduleId= 0

    private var boardId= 0

    private var sprintId= 0

    private var selectedProjectId = 0

    private var selectedModuleId = 0

    private var selectedIssueTypeId = 0

    private var selectedWorkFlowId = 0

    private var selectedBoardId = 0

    private var selectedSprintId = 0

    private var detailsProjectId = 0

    private var detailsModuleId = 0

    private var detailsIssueTypeId = 0

    private var detailsWorkFlowId = 0

    private var detailsReportedBy = 0

    private var detailsBoardId = 0

    private var detailsSprintId = 0

    private var projectName: String? = null

    private var issueName: String? = null

    private var workFlowName: String? = null

    private var reportedByName: String? = null

    private var moduleName: String? = null

    private var boardName: String? = null

    private var sprintName: String? = null

    private var errorMsg: String? = null

    private var selectedPriorityValue: String? = null

    private var selectedFilePath: String? = null

    private var detailsTaskName: String? = null

    private var detailsSource: String? = null

    private var detailsDescription: String? = null

    private var detailsAnalysis: String? = null

    private var detailsTicketNumber: String? = null

    private var detailsPriority: String? = null

    private var detailsIsActiveId:Boolean? = null

    private var detailsIsBillableId:Boolean? = null

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

    private var sourceType: ArrayAdapter<*>? = null

    var sourceList = arrayOf<String?>("Select", "Issue", "Task", "Ticket","Email")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sub_module)
        binding!!.cardView.visibility = View.VISIBLE
        bundleData()
        setAdapter(8)
        getBackLogDetails(projectTaskId)
        initListener()

        binding!!.projectNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    binding!!.cardView.visibility = View.VISIBLE
                    selectedProjectId = projectIdList[position]
                    getModuleType(selectedProjectId)
                    getBoardType(selectedProjectId)
                    getSprintType(selectedProjectId)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.moduleEpicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedModuleId = moduleIdList[position]
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.issueTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedIssueTypeId = issueIdList[position]
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.workFlowSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedWorkFlowId = workFlowIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.boardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedBoardId = boardIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.sprintSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedSprintId = sprintIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
        binding!!.chooseFile.setOnClickListener(onClickListener)

    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.chooseFile -> doBrowseFile()
            R.id.btnSave -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this))
                {
                    binding!!.cardView.visibility = View.VISIBLE
                    UpdateBackLog()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun bundleData()
    {
        val intent = intent
        projectTaskId = intent.getIntExtra("ProjectTaskId", 0)
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
                        getIssueType()
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
            Request.Method.POST,
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
                            issueIdList.add(issueId)
                            issueNameList.add(issueName)
                        }
                        setAdapter(2)
                        getWorkFlowType()
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
                        getReportedByType()
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
            Request.Method.POST,
            Api.getBackLogReportedType + SharedPref.getCompanyId(this) ,
            { ServerResponse ->
                try {
                    binding!!.cardView.visibility = View.GONE
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
                            reportedByIdList.add(reportedById)
                            reportedByNameList.add(reportedByName)
                        }
                        setAdapter(4)
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
            Request.Method.POST,
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
            Request.Method.POST,
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
            Request.Method.POST,
            Api.getBackLogSprintType + projectIdSelected ,
            { ServerResponse ->
                try {
                    binding!!.cardView.visibility = View.GONE
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
            getSpinnerIndex(binding!!.projectNameSpinner, projectIdList, detailsProjectId, true)
        }
        else if (id == 2)
        {
            issueNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, issueNameList)
            issueNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.issueTypeSpinner.adapter = issueNameType
            getSpinnerIndex(binding!!.issueTypeSpinner, issueIdList, detailsIssueTypeId, false)
        }
        else if (id == 3)
        {
            workFlowType = ArrayAdapter(this, android.R.layout.simple_spinner_item, workFlowNameList)
            workFlowType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.workFlowSpinner.adapter = workFlowType
            getSpinnerIndex(binding!!.workFlowSpinner, workFlowIdList, detailsWorkFlowId, false)
        }
        else if (id == 4)
        {
            reportedByType = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportedByNameList)
            reportedByType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.reportedBySpinner.adapter = reportedByType
            getSpinnerIndex(binding!!.reportedBySpinner, reportedByIdList, detailsReportedBy, true)
        }
        else if (id == 5)
        {
            moduleType = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.moduleEpicSpinner.adapter = moduleType
            getSpinnerIndex(binding!!.moduleEpicSpinner, moduleIdList, detailsModuleId, false)
        }
        else if (id == 6)
        {
            boardType = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.boardSpinner.adapter = boardType
            getSpinnerIndex(binding!!.boardSpinner, boardIdList, detailsBoardId, false)
        }
        else if (id == 7)
        {
            sprintType = ArrayAdapter(this, android.R.layout.simple_spinner_item, sprintNameList)
            sprintType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.sprintSpinner.adapter = sprintType
            getSpinnerIndex(binding!!.sprintSpinner, sprintIdList, detailsSprintId, false)
        }
        else if (id == 8)
        {
            sourceType = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, sourceList)
            sourceType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.sourceSpinner.adapter = sourceType
        }
    }

    fun getSpinnerIndex(requiredSpinner: Spinner, spinnerList: List<Int>, mySetId: Int, action: Boolean) {
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

    private fun validateDetails(): Boolean {
        if (selectedProjectId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Project")
            return false
        }
        else if (selectedModuleId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Module Type")
            return false
        }
        else if (selectedIssueTypeId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Issue Type")
            return false
        }
        else if (binding!!.etIssueName.text.toString().isEmpty())
        {
            binding!!.etIssueName.error = "Enter Issue Name"
            return false
        }
        else if (selectedWorkFlowId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Work Flow Type")
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
            selectedFilePath = getRealPathFromURI(selectedFile)
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        var filePath: String? = null
        var file: File?
        try {
            file = File(uri!!.path!!)
            filePath = file.path
        } catch (e: Exception) {
            CommonMethod.showToast(this, e.toString())
        }
        return filePath
    }

    fun getBackLogDetails(id: Int) {
        stringRequest = object : StringRequest(Method.GET, Api.getBackLogDetailsById + id, Response.Listener
        { ServerResponse ->
            try {
                val jsondata = JSONObject(ServerResponse)
                status = jsondata.getInt("status")
                if (status == 200) {
                    val dataArray = jsondata.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val loginObject = dataArray.getJSONObject(i)
                        detailsProjectId = loginObject.getInt("ProjectID")
                        detailsModuleId = loginObject.getInt("ModuleID")
                        detailsIssueTypeId = loginObject.getInt("ModuleTypeID")
                        detailsTaskName = loginObject.getString("TaskName")
                        detailsWorkFlowId = loginObject.getInt("TaskCategory")
                        detailsIsActiveId = loginObject.getBoolean("Active")
                        detailsIsBillableId = loginObject.getBoolean("Billable")
                        detailsReportedBy = loginObject.getInt("ReportBy")
                        detailsSprintId = loginObject.getInt("SprintID")
                        detailsBoardId = loginObject.getInt("BoardID")
                        detailsAnalysis = loginObject.getString("Analysis")
                        detailsDescription = loginObject.getString("Description")
                        detailsSource = loginObject.getString("Source")
                        detailsTicketNumber = loginObject.getString("TicketNo")
                        detailsPriority = loginObject.getString("Priority")
                    }

                    binding!!.etIssueName.setText(detailsTaskName)
                    binding!!.etDescription.setText(detailsDescription)
                    binding!!.etTaskAnalysis.setText(detailsAnalysis)
                    if (detailsIsActiveId!!) binding!!.isActiveCb.isChecked = true else binding!!.isActiveCb.isChecked = false
                    if (detailsIsBillableId!!) binding!!.isBillableCb.isChecked = true else binding!!.isBillableCb.isChecked = false
                    val selectedIndex = sourceList.indexOf(detailsSource)
                    if (selectedIndex != -1) {
                        binding!!.sourceSpinner.setSelection(selectedIndex)
                    }
                    when (detailsPriority) {
                        "Low" -> binding!!.lowRadioButton.isChecked = true
                        "Medium" -> binding!!.mediumRadioButton.isChecked = true
                        "High" -> binding!!.highRadioButton.isChecked = true
                        "Critical" -> binding!!.criticalRadioButton.isChecked = true
                    }
                    binding!!.etTicketNo.setText(detailsTicketNumber)
                    projectList()

                }
                else
                {
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

    fun UpdateBackLog()
    {
        binding!!.cardView.visibility = View.VISIBLE
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, Api.updateBackLog,
            Response.Listener<JSONObject> { jsondata ->
                binding!!.cardView.visibility = View.GONE
                try {
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Updated Successfully", ignoreCase = true)) {
                            CommonMethod.showToast(this, errorMsg)
                            finish()
                        }
                    } else {
                        CommonMethod.showToast(this, " Wrong")
                    }

                } catch (e: JSONException) {
                    CommonMethod.showToast(this, "Something Went Wrong")
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                binding!!.cardView.visibility = View.GONE
                CommonMethod.showToast(this, "Error")
            }
        )
        {
            override fun getByteData(): Map<String, DataPart>? {
                val dataMap = HashMap<String, DataPart>()
//                val file = File(selectedFilePath!!)
//                val fileContent = file.readBytes()
//                val fileName = file.name
//                dataMap["FileUpload"] = DataPart(fileName, fileContent)
                return dataMap
            }

            override fun getParams(): Map<String, String> {
                if (binding!!.priorityRadioGroup.checkedRadioButtonId != -1) {
                    val selectedRadioButton =
                        findViewById<RadioButton>(binding!!.priorityRadioGroup.checkedRadioButtonId)
                    selectedPriorityValue = selectedRadioButton.text.toString()
                }

                val params = HashMap<String, String>()
                params["CompanyID"] = SharedPref.getCompanyId(this@EditSubModule).toString()
                params["IssueName"] = binding!!.etIssueName.text.toString()
                params["ProjectName"] = selectedProjectId.toString()
                params["WorkFlow"] = selectedWorkFlowId.toString()
                params["Description"] = binding!!.etDescription.text.toString()
                params["Analysis"] = binding!!.etTaskAnalysis.text.toString()
                params["Module"] = selectedModuleId.toString()
                params["IssueType"] = selectedIssueTypeId.toString()
                params["UserName"] = SharedPref.getUserName(this@EditSubModule).toString()
                params["BoardID"] = selectedBoardId.toString()
                params["SprintID"] = selectedSprintId.toString()
                params["Billable"] = if (binding!!.isBillableCb.isChecked) "true" else "false"
                params["AllotedBy"] = SharedPref.getEmployeeId(this@EditSubModule).toString()
                params["Source"] = binding!!.sourceSpinner.selectedItem.toString()
                params["Priority"] = selectedPriorityValue.toString()
                params["Active"] = if (binding!!.isActiveCb.isChecked) "true" else "false"
                params["PrjTaskID"] = projectTaskId.toString()
                return params
            }
        }
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
    }
}