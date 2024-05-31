package com.androidx.helpdesk.editQuickProject.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.androidx.helpdesk.databinding.ActivityQuickEditSubModuleBinding
import com.androidx.helpdesk.editQuickProject.adapter.EditSubModuleAdapter
import com.androidx.helpdesk.editQuickProject.model.EditSubModuleModel
import com.androidx.helpdesk.quickProject.edit.EditSubModule
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class QuickEditSubModule : AppCompatActivity() {

    private var binding: ActivityQuickEditSubModuleBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var boardId = 0

    private var issueId = 0

    private var workFlowId = 0

    private var sprintId = 0

    private var moduleId = 0

    private var reportedById = 0

    private var bundleModuleId = 0

    private var subModuleId = 0

    private var subModuleProjectTaskId = 0

    private var subModuleEstimatedHours = 0

    private var spinnerProjectId = 0

    private var selectedProjectId= 0

    private var selectedModuleId= 0

    private var selectedWorkFlowId= 0

    private var selectedIssueTypeId= 0

    private var selectedSprintId= 0

    private var selectedBoardId= 0

    private var MY_SOCKET_TIMEOUT_MS = 300000

    private var extendedUrl: String? = null

    private var projectName: String? = null

    private var subModuleName: String? = null

    private var subModuleTaskName: String? = null

    private var subModuleTaskCategory: String? = null

    private var errorMsg: String? = null

    private var selectedPriorityValue: String? = null

    private var spinnerProjectName: String? = null

    private var moduleName: String? = null

    private var workFlowName: String? = null

    private var issueName: String? = null

    private var reportedByName: String? = null

    private var boardName: String? = null

    private var sprintName: String? = null

    private var editSubModuleList: MutableList<EditSubModuleModel> = ArrayList()

    private var editSubModuleAdapter: EditSubModuleAdapter? = null

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

    var sourceList = arrayOf<String?>("Select", "Issue", "Task", "Ticket","Email")

    private var projectNameType: ArrayAdapter<*>? = null

    private var issueNameType: ArrayAdapter<*>? = null

    private var workFlowType: ArrayAdapter<*>? = null

    private var reportedByType: ArrayAdapter<*>? = null

    private var moduleType: ArrayAdapter<*>? = null

    private var boardType: ArrayAdapter<*>? = null

    private var sprintType: ArrayAdapter<*>? = null

    private var sourceType: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quick_edit_sub_module)
        binding!!.cardView.visibility = View.VISIBLE
        getBundleData()
        initListener()
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        bundleModuleId = mIntent.getIntExtra("bundleModuleId",0)
        if(bundleModuleId==0)
        {
            extendedUrl= SharedPref.getCompanyId(this) + "&ProjectID=" + projectId
        }
        else{
            extendedUrl= SharedPref.getCompanyId(this) + "&ProjectID=" + projectId + "&ModuleID=" + bundleModuleId.toString()
        }
        projectList()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.addSubModule.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.addSubModule ->
            {
                popUpLayout(view)
            }
            R.id.btnNext ->
            {
                startActivity(Intent(this, QuickEditEstimate::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
            }
        }
    }

    private fun subModuleEdit(pid: Int?,mid: Int?) {
        startActivity(Intent(this, EditSubModule::class.java).putExtra("projectId",pid).putExtra("ProjectTaskId",mid))
    }
    private fun getSubModuleList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        editSubModuleList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getSubModuleList + extendedUrl,
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
                                subModuleId = loginObject.getInt("ModuleID")
                                subModuleProjectTaskId = loginObject.getInt("PrjTaskID")
                                subModuleName = loginObject.getString("ModuleName")
                                subModuleTaskName = loginObject.getString("TaskName")
                                subModuleTaskCategory = loginObject.getString("Description")
                                subModuleEstimatedHours = loginObject.getInt("EstTotalHours")
                                editSubModuleList.add(EditSubModuleModel(subModuleId,subModuleProjectTaskId, subModuleName,subModuleTaskName,subModuleTaskCategory,subModuleEstimatedHours))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            editSubModuleAdapter = EditSubModuleAdapter(this, editSubModuleList)
                            binding!!.recyclerView.adapter = editSubModuleAdapter
                            editSubModuleAdapter!!.notifyDataSetChanged()
                            editSubModuleAdapter!!.setOnClickListener(object :
                                EditSubModuleAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        subModuleDelete(model)
                                    }
                                    else
                                    {
                                        subModuleEdit(projectId,model)
                                    }
                                }
                            })
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.btnNext.visibility = View.GONE   //need to change gone
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
//        stringRequest!!.retryPolicy = object : RetryPolicy {
//            override fun getCurrentTimeout(): Int {
//                return MY_SOCKET_TIMEOUT_MS
//            }
//
//            override fun getCurrentRetryCount(): Int {
//                return MY_SOCKET_TIMEOUT_MS
//            }
//
//            @Throws(VolleyError::class)
//            override fun retry(error: VolleyError) {
//            }
//        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun subModuleDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.deleteBackLog + id,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            CommonMethod.showToast(this, "Deleted Successfully")
                            getSubModuleList()
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

    private fun popUpLayout(view: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.add_sub_module_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpProjectNameSpinner = popupView.findViewById(R.id.projectNameSpinner) as Spinner
        val popUpModuleEpicSpinner=popupView.findViewById(R.id.moduleEpicSpinner) as Spinner
        val popUpIssueSpinner=popupView.findViewById(R.id.issueTypeSpinner) as Spinner
        val popUpIssueName=popupView.findViewById(R.id.etIssueName) as EditText
        val popUpWorkFlowSpinner=popupView.findViewById(R.id.workFlowSpinner) as Spinner
        val popUpPriorityRadioGroup=popupView.findViewById(R.id.priorityRadioGroup) as RadioGroup
        val popUpBoardSpinner=popupView.findViewById(R.id.boardSpinner) as Spinner
        val popUpSprintSpinner=popupView.findViewById(R.id.sprintSpinner) as Spinner
        val popUpDescription=popupView.findViewById(R.id.etDescription) as EditText
        val popUpTaskAnalysis=popupView.findViewById(R.id.etTaskAnalysis) as EditText
        val popUpReportedBySpinner=popupView.findViewById(R.id.reportedBySpinner) as Spinner
        val popUpIsActive=popupView.findViewById(R.id.isActiveCb) as CheckBox
        val popUpIsBillable=popupView.findViewById(R.id.isBillableCb) as CheckBox
        val popUpSourceSpinner=popupView.findViewById(R.id.sourceSpinner) as Spinner
        val popUpChooseFile=popupView.findViewById(R.id.chooseFile) as Button
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddSubModule) as Button

        setAdapter(1,popUpProjectNameSpinner)
        setAdapter(2,popUpIssueSpinner)
        setAdapter(2,popUpWorkFlowSpinner)
        setAdapter(2,popUpReportedBySpinner)
        setAdapter(8,popUpSourceSpinner)


        popUpProjectNameSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    if (parentView!!.getItemAtPosition(position) != "Select")
                    {
                        binding!!.cardView.visibility = View.VISIBLE
                        selectedProjectId = projectIdList[position]
                        getModuleType(popUpModuleEpicSpinner,selectedProjectId)
                        getBoardType(popUpBoardSpinner,selectedProjectId)
                        getSprintType(popUpSprintSpinner,selectedProjectId)
                    }
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        popUpModuleEpicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedModuleId = moduleIdList[position]
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        popUpIssueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedIssueTypeId = issueIdList[position]
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        popUpWorkFlowSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedWorkFlowId = workFlowIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        popUpBoardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedBoardId = boardIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        popUpSprintSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedSprintId = sprintIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }


        popUpSaveBtn.setOnClickListener()
        {

            if (selectedProjectId == 0)
            {
                CommonMethod.showToast(this, "Select Project")
            }
            else if (selectedModuleId == 0)
            {
                CommonMethod.showToast(this, "Select Module Type")
            }
            else if (selectedIssueTypeId == 0)
            {
                CommonMethod.showToast(this, "Select Issue Type")
            }
            else if (popUpIssueName.text.toString().isEmpty())
            {
                popUpIssueName.error = "Enter Issue Name"
            }
            else if (selectedWorkFlowId == 0)
            {
                CommonMethod.showToast(this, "Select Work Flow Type")
            }
            else
            {
                popupWindow.dismiss()
                val billCb = if (popUpIsBillable.isChecked) "true" else "false"
                val activeCb = if (popUpIsActive.isChecked) "true" else "false"
                if (popUpPriorityRadioGroup.checkedRadioButtonId != -1) {
                    val selectedRadioButton =
                        findViewById<RadioButton>(popUpPriorityRadioGroup.checkedRadioButtonId)
                    selectedPriorityValue = selectedRadioButton.text.toString()
                }
                createBackLog(popUpIssueName.text.toString(),
                    selectedProjectId.toString(),
                    selectedWorkFlowId.toString(),
                    popUpDescription.text.toString(),
                    popUpTaskAnalysis.text.toString(),
                    selectedModuleId.toString(),
                    selectedIssueTypeId.toString(),
                    selectedBoardId.toString(),
                    selectedSprintId.toString(),
                    billCb,
                    popUpSourceSpinner.selectedItem.toString(),
                    selectedPriorityValue!!,
                    activeCb)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        getSubModuleList()
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
                            spinnerProjectId = loginObject.getInt("ProjectID")
                            spinnerProjectName = loginObject.getString("ProjectName")
                            projectIdList.add(spinnerProjectId)
                            projectNameList.add(spinnerProjectName)
                        }
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
                        getSubModuleList()
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

    private fun getModuleType(popUpModuleEpicSpinner: Spinner,projectIdSelected: Int)
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
                        setAdapter(5,popUpModuleEpicSpinner)
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

    private fun getBoardType(popUpBoardSpinner: Spinner,projectIdSelected: Int)
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
                        setAdapter(6,popUpBoardSpinner)
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

    private fun getSprintType(popUpSprintSpinner: Spinner,projectIdSelected: Int)
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
                        setAdapter(7,popUpSprintSpinner)
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

    fun setAdapter(id: Int,spinnerId: Spinner) {
        if (id == 1) {
            projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
            projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = projectNameType
            getSpinnerIndex(spinnerId, projectIdList,projectId, false)
        } else if (id == 2) {
            issueNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, issueNameList)
            issueNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = issueNameType
        }
        else if (id == 3) {
            workFlowType = ArrayAdapter(this, android.R.layout.simple_spinner_item, workFlowNameList)
            workFlowType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = workFlowType
        }
        else if (id == 4) {
            reportedByType = ArrayAdapter(this, android.R.layout.simple_spinner_item, reportedByNameList)
            reportedByType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = reportedByType
        }
        else if (id == 5)
        {
            moduleType = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = moduleType
            getSpinnerIndex(spinnerId, moduleIdList, bundleModuleId, false)
        }
        else if (id == 6)
        {
            boardType = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = boardType
        }
        else if (id == 7)
        {
            sprintType = ArrayAdapter(this, android.R.layout.simple_spinner_item, sprintNameList)
            sprintType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = sprintType
        }
        else if (id == 8)
        {
            sourceType = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, sourceList)
            sourceType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = sourceType
        }
    }

    fun createBackLog(uploadIssueName: String,uploadProjectId: String,uploadWorkFlowId: String,uploadDescription: String,uploadTaskAnalysis: String,uploadModuleId: String,uploadIssueTypeId: String,uploadBoardId: String,uploadSprintId: String,uploadBillable: String,uploadSource: String,uploadPriority: String,uploadActive: String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, Api.insertNewBackLog,
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
                        if (errorMsg.equals("Record Inserted", ignoreCase = true)) {
                            CommonMethod.showToast(this, errorMsg)
                            getSubModuleList()
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
                val params = HashMap<String, String>()
                params["CompanyID"] = SharedPref.getCompanyId(this@QuickEditSubModule).toString()
                params["IssueName"] = uploadIssueName
                params["ProjectName"] = uploadProjectId
                params["WorkFlow"] = uploadWorkFlowId
                params["Description"] = uploadDescription
                params["Analysis"] = uploadTaskAnalysis
                params["Module"] = uploadModuleId
                params["IssueType"] = uploadIssueTypeId
                params["UserName"] = SharedPref.getUserName(this@QuickEditSubModule).toString()
                params["BoardID"] = uploadBoardId
                params["SprintID"] = uploadSprintId
                params["Billable"] = uploadBillable
                params["AllotedBy"] = SharedPref.getEmployeeId(this@QuickEditSubModule).toString()
                params["Source"] = uploadSource
                params["Priority"] = uploadPriority
                params["Active"] = uploadActive
                return params
            }
        }
        Volley.newRequestQueue(this).add(volleyMultipartRequest)
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
}