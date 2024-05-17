package com.androidx.helpdesk.quickProject.view

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityCreateSubModuleBinding
import com.androidx.helpdesk.quickProject.adapter.SubModuleAdapter
import com.androidx.helpdesk.quickProject.edit.EditSubModule
import com.androidx.helpdesk.quickProject.model.SubModuleModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CreateSubModule : AppCompatActivity() {

    private var binding: ActivityCreateSubModuleBinding? = null

    private var status = 0

    private var projectId = 0

    private var subModuleId = 0

    private var moduleId = 0

    private var taskCategoryId = 0

    private var issueId = 0

    private var subModuleEstimatedHours = 0

    private var phaseId = 0

    private var subModuleProjectTaskId = 0

    private var moduleName: String? = null

    private var issueName: String? = null

    private var taskCategoryName: String? = null

    private var errMsg: String? = null

    private var errorMsg: String? = null

    private var projectName: String? = null

    private var subModuleName: String? = null

    private var subModuleTaskName: String? = null

    private var phaseDescription: String? = null

    private var subModuleTaskCategory: String? = null

    private var stringRequest: StringRequest? = null

    private var subModuleList: MutableList<SubModuleModel> = ArrayList()

    private val moduleIdList: MutableList<Int> = ArrayList()

    private val moduleNameList: MutableList<String?> = ArrayList()

    private val phaseIdList: MutableList<Int> = ArrayList()

    private val phaseDescriptionList: MutableList<String?> = ArrayList()

    private val taskCategoryIdList: MutableList<Int> = ArrayList()

    private val taskCategoryNameList: MutableList<String?> = ArrayList()

    private val issueIdList: MutableList<Int> = ArrayList()

    private val issueNameList: MutableList<String?> = ArrayList()

    private var subModuleAdapter: SubModuleAdapter? = null

    private var priorityList = arrayOf<String?>("Select","Low", "Medium", "High", "Critical")

    private var taskStatusList = arrayOf<String?>("Unassigned", "Assigned", "InProgress", "Completed")

    private var priorityAdapter: ArrayAdapter<*>? = null

    private var taskStatusAdapter: ArrayAdapter<*>? = null

    private var moduleAdapter: ArrayAdapter<*>? = null

    private var taskCategoryAdapter: ArrayAdapter<*>? = null

    private var issueAdapter: ArrayAdapter<*>? = null

    private var phaseAdapter: ArrayAdapter<*>? = null

    private var selectedModuleId = 0

    private var selectedPhaseId = 0

    private var selectedTaskCategoryId = 0

    private var selectedIssueTypeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_sub_module)
        binding!!.cardView.visibility = View.VISIBLE
        getBundleData()
        initListener()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.addSubModule.setOnClickListener(onClickListener)
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        getModuleList()
    }

    private fun subModuleEdit(pid: Int?,mid: Int?) {
        startActivity(Intent(this, EditSubModule::class.java).putExtra("projectId",pid).putExtra("subModulePrjTaskId",mid))
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
                startActivity(Intent(this, CreateEstimate::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
            }
        }
    }

    private fun getSubModuleList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        subModuleList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getSubModuleList + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
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
                                subModuleList.add(SubModuleModel(subModuleId,subModuleProjectTaskId, subModuleName,subModuleTaskName,subModuleTaskCategory,subModuleEstimatedHours))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            subModuleAdapter = SubModuleAdapter(this, subModuleList)
                            binding!!.recyclerView.adapter = subModuleAdapter
                            subModuleAdapter!!.notifyDataSetChanged()
                            subModuleAdapter!!.setOnClickListener(object :
                                SubModuleAdapter.OnClickListener {
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
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddSubModule) as Button
        val popUpScreenName=popupView.findViewById(R.id.screenNameValue) as EditText
        val popUpEstimatedHours=popupView.findViewById(R.id.estimatedHoursValue) as EditText
        val popUpStartDate=popupView.findViewById(R.id.startDate) as EditText
        val popUpEndDate=popupView.findViewById(R.id.endDate) as EditText
        val popUpIsActive=popupView.findViewById(R.id.isActive) as CheckBox
        val popUpIsBillable=popupView.findViewById(R.id.isBillable) as CheckBox
        val popUpModuleSpinner=popupView.findViewById(R.id.moduleSpinner) as Spinner
        val popUpPhaseSpinner=popupView.findViewById(R.id.phaseSpinner) as Spinner
        val popUpTaskCategorySpinner=popupView.findViewById(R.id.taskCategorySpinner) as Spinner
        val popUpIssueTypeSpinner=popupView.findViewById(R.id.issueTypeSpinner) as Spinner
        val popUpPrioritySpinner=popupView.findViewById(R.id.prioritySpinner) as Spinner
        val popUpTaskStatusSpinner=popupView.findViewById(R.id.taskStatusSpinner) as Spinner

        setAdapter(1,popUpPrioritySpinner)
        setAdapter(2,popUpTaskStatusSpinner)
        setAdapter(3,popUpModuleSpinner)
        setAdapter(4,popUpTaskCategorySpinner)
        setAdapter(5,popUpIssueTypeSpinner)
        setAdapter(6,popUpPhaseSpinner)

        //getting values from spinner

        popUpModuleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedModuleId = moduleIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        popUpPhaseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedPhaseId = phaseIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        popUpTaskCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedTaskCategoryId = taskCategoryIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        popUpIssueTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedIssueTypeId = issueIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }

        popUpStartDate.setOnClickListener()
        {
            setDate(popUpStartDate)
        }

        popUpEndDate.setOnClickListener()
        {
            setDate(popUpEndDate)
        }


        popUpSaveBtn.setOnClickListener()
        {
            if (selectedModuleId == null || selectedModuleId == 0 || selectedModuleId.equals(0)) {
                CommonMethod.Companion.showToast(this, "Please Select the Module")
            }
            else if (popUpScreenName.text.toString() == null || popUpScreenName.text.toString().isEmpty())
            {
                popUpScreenName.error = "Enter Screen Name"
            }
            else if (selectedPhaseId == null || selectedPhaseId == 0 || selectedPhaseId.equals(0))
            {
                CommonMethod.Companion.showToast(this, "Please Select the Phase Type")
            }
            else if (selectedTaskCategoryId == null || selectedTaskCategoryId == 0 || selectedTaskCategoryId.equals(0))
            {
                CommonMethod.Companion.showToast(this, "Please Select the Task Category")
            }
            else if (selectedIssueTypeId == null || selectedIssueTypeId == 0 || selectedIssueTypeId.equals(0))
            {
                CommonMethod.Companion.showToast(this, "Please Select the Issue Type")
            }
            else if (popUpStartDate.text.toString() == null || popUpStartDate.text.toString().isEmpty())
            {
                popUpStartDate.error = "Enter Start Date"
            }
            else if (popUpEndDate.text.toString() == null || popUpEndDate.text.toString().isEmpty())
            {
                popUpEndDate.error = "Enter End Date"
            }
            else if (popUpEstimatedHours.text.toString() == null || popUpEstimatedHours.text.toString().isEmpty())
            {
                popUpEstimatedHours.error = "Enter Estimated Hours"
            }
            else if (popUpPrioritySpinner.selectedItem.toString() == "Select" || popUpPrioritySpinner.selectedItem.toString().equals("Select"))
            {
                CommonMethod.Companion.showToast(this, "Please Select the Task Priority")
            }
            else if (popUpTaskStatusSpinner.selectedItem.toString() == "Select" || popUpTaskStatusSpinner.selectedItem.toString().equals("Select"))
            {
                CommonMethod.Companion.showToast(this, "Please Select the Task Priority")
            }
            else
            {
                popupWindow.dismiss()
                addSubModule(selectedModuleId,popUpScreenName.text.toString(), selectedTaskCategoryId,popUpIsActive.isChecked,popUpIsBillable.isChecked,popUpStartDate.text.toString(),selectedIssueTypeId,popUpEndDate.text.toString(),popUpEstimatedHours.text.toString(),popUpTaskStatusSpinner.selectedItem.toString(),selectedPhaseId,popUpPrioritySpinner.selectedItem.toString())
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
            priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityList)
            priorityAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = priorityAdapter
        } else if (id == 2) {
            taskStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskStatusList)
            taskStatusAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = taskStatusAdapter
        }
        else if (id == 3) {
            moduleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = moduleAdapter
        }
        else if (id == 4) {
            taskCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskCategoryNameList)
            taskCategoryAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = taskCategoryAdapter
        }
        else if (id == 5) {
            issueAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, issueNameList)
            issueAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = issueAdapter
        }
        else if (id == 6) {
            phaseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, phaseDescriptionList)
            phaseAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = phaseAdapter
        }
    }

    private fun getModuleList()
    {
        moduleIdList.clear()
        moduleNameList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getModule + SharedPref.getCompanyId(this) +"&ProjectID=" + projectId,
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
                        getPhaseList()
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

    private fun getPhaseList()
    {
        phaseIdList.clear()
        phaseDescriptionList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getPhase,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        phaseDescriptionList.add("Select")
                        phaseIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            phaseId = loginObject.getInt("PhaseID")
                            phaseDescription = loginObject.getString("PhaseDescription")
                            phaseIdList.add(phaseId)
                            phaseDescriptionList.add(phaseDescription)
                        }
                        getTaskCategory()
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

    private fun getTaskCategory()
    {
        taskCategoryIdList.clear()
        taskCategoryNameList.clear()
        stringRequest = StringRequest(Request.Method.GET, Api.getTaskCategory + SharedPref.getCompanyId(this),
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        taskCategoryNameList.add("Select")
                        taskCategoryIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            taskCategoryId = loginObject.getInt("TaskCategoryID")
                            taskCategoryName = loginObject.getString("Description")
                            taskCategoryIdList.add(taskCategoryId)
                            taskCategoryNameList.add(taskCategoryName)
                        }
                        getIssueType()
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
        stringRequest = StringRequest(Request.Method.GET, Api.getIssueType,
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
                        getSubModuleList()
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

    private fun addSubModule(moduleId: Int,taskName: String,taskCategoryId: Int,isActive: Boolean,isBillable: Boolean,startDate: String,taskType: Int,endDate: String,estHours :String,taskStatus: String,phaseValue: Int,priorityValue: String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.addSubModule + SharedPref.getCompanyId(this) +"&ProjectID="+ projectId + "&ModuleID=" + moduleId + "&TaskName=" + taskName + "&TaskCategory=" + taskCategoryId +"&Active=" + isActive +"&Billable=" + isBillable + "&EstStartDate=" + startDate + "&TaskType=" + taskType + "&EstEndDate=" + endDate + "&EstTotalHours=" + estHours + "&UserName=" + SharedPref.getCompanyId(this) + "&CardviewStatus=" + taskStatus + "&PhaseDescription=" + phaseValue + "&Priority=" + priorityValue,
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
                            if (errMsg.equals("Inserted Successfully") || errMsg == "Inserted Successfully") {
                                CommonMethod.showToast(this, "Added Successfully..!")
                                getSubModuleList()
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

    private fun subModuleDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.deleteSubModuleById + id,
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
                                getSubModuleList()
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
        getSubModuleList()
    }
}