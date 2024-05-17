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
import com.androidx.helpdesk.databinding.ActivityEditSubModuleBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EditSubModule : AppCompatActivity() {

    private var binding: ActivityEditSubModuleBinding? = null

    private var stringRequest: StringRequest? = null

    private var projectId = 0

    private var projectTaskId = 0

    private var moduleId = 0

    private var phaseId = 0

    private var status = 0

    private var taskCategoryId = 0

    private var issueId = 0

    private var subModuleId = 0

    private var subModuleTaskCategory = 0

    private var subModulePhaseDescription = 0

    private var selectedModuleId = 0

    private var selectedPhaseId = 0

    private var selectedTaskCategoryId = 0

    private var selectedIssueTypeId = 0

    private var subModuleTaskName: String? = null

    private var errMsg: String? = null

    private var subModuleEstHours: String? = null

    private var subModuleTaskType: String? = null

    private var subModuleCardViewStatus: String? = null

    private var subModuleStartDate: String? = null

    private var subModuleEndDate: String? = null

    private var subModulePriority: String? = null

    private var subModuleIsActive: Boolean? = null

    private var subModuleIsBillable: Boolean? = null

    private var moduleName: String? = null

    private var phaseDescription: String? = null

    private var taskCategoryName: String? = null

    private var issueName: String? = null

    private val moduleIdList: MutableList<Int> = ArrayList()

    private val moduleNameList: MutableList<String?> = ArrayList()

    private val phaseIdList: MutableList<Int> = ArrayList()

    private val phaseDescriptionList: MutableList<String?> = ArrayList()

    private val taskCategoryIdList: MutableList<Int> = ArrayList()

    private val taskCategoryNameList: MutableList<String?> = ArrayList()

    private val issueIdList: MutableList<Int> = ArrayList()

    private val issueNameList: MutableList<String?> = ArrayList()

    private var priorityList = arrayOf<String?>("Select","Low", "Medium", "High", "Critical")

    private var taskStatusList = arrayOf<String?>("Unassigned", "Assigned", "InProgress", "Completed")

    private var priorityAdapter: ArrayAdapter<*>? = null

    private var taskStatusAdapter: ArrayAdapter<*>? = null

    private var moduleAdapter: ArrayAdapter<*>? = null

    private var taskCategoryAdapter: ArrayAdapter<*>? = null

    private var issueAdapter: ArrayAdapter<*>? = null

    private var phaseAdapter: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sub_module)
        setAdapter(1)
        setAdapter(2)
        getBundleData()
        initListener()

        binding!!.moduleSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedModuleId = moduleIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.phaseSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedPhaseId = phaseIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.taskCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedTaskCategoryId = taskCategoryIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

        binding!!.issueTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long)
                {
                    selectedIssueTypeId = issueIdList[position]
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
        binding!!.startDate.setOnClickListener(onClickListener)
        binding!!.endDate.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.startDate ->
            {
                setDate(binding!!.startDate)
            }
            R.id.endDate ->
            {
                setDate(binding!!.endDate)
            }
            R.id.btnSave ->
            {
                if (selectedModuleId == null || selectedModuleId == 0 || selectedModuleId.equals(0))
                {
                    CommonMethod.Companion.showToast(this, "Please Select the Module")
                }
                else if (binding!!.screenNameValue.text.toString() == null || binding!!.screenNameValue.text.toString().isEmpty())
                {
                    binding!!.screenNameValue.error = "Enter Screen Name"
                }
                else if (selectedPhaseId == null || selectedPhaseId == 0 || selectedPhaseId.equals(0))
                {
                    CommonMethod.Companion.showToast(this, "Please Select the Phase Type")
                }
                else if (selectedTaskCategoryId == null || selectedTaskCategoryId == 0 || selectedTaskCategoryId.equals(0))
                {
                    CommonMethod.Companion.showToast(this, "Please Select the Task Category")
                }
                else if (selectedIssueTypeId == null || selectedIssueTypeId == 0 || selectedIssueTypeId.equals(0)) {
                    CommonMethod.Companion.showToast(this, "Please Select the Issue Type")
                }
                else if ( binding!!.startDate.text.toString() == null || binding!!.startDate.text.toString().isEmpty())
                {
                    binding!!.startDate.error = "Enter Start Date"
                }
                else if (binding!!.endDate.text.toString() == null || binding!!.endDate.text.toString().isEmpty())
                {
                    binding!!.endDate.error = "Enter End Date"
                }
                else if (binding!!.estimatedHoursValue.text.toString() == null || binding!!.estimatedHoursValue.text.toString().isEmpty())
                {
                    binding!!.estimatedHoursValue.error = "Enter Estimated Hours"
                }
                else if (binding!!.prioritySpinner.selectedItem.toString() == "Select" || binding!!.prioritySpinner.selectedItem.toString().equals("Select"))
                {
                    CommonMethod.Companion.showToast(this, "Please Select the Task Priority")
                }
                else if (binding!!.taskStatusSpinner.selectedItem.toString() == "Select" || binding!!.taskStatusSpinner.selectedItem.toString().equals("Select"))
                {
                    CommonMethod.Companion.showToast(this, "Please Select the Task Priority")
                }
                else
                {
                    updateSubModule(selectedModuleId,binding!!.screenNameValue.text.toString(), selectedTaskCategoryId,binding!!.isActive.isChecked,binding!!.isBillable.isChecked,binding!!.startDate.text.toString(),selectedIssueTypeId,binding!!.endDate.text.toString(),binding!!.estimatedHoursValue.text.toString(),binding!!.taskStatusSpinner.selectedItem.toString(),selectedPhaseId,binding!!.prioritySpinner.selectedItem.toString())
                }
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


    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectTaskId = mIntent.getIntExtra("subModulePrjTaskId",0)
        getModuleList()
    }

    private fun getModuleList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        moduleIdList.clear()
        moduleNameList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getModule + SharedPref.getCompanyId(this) +"&ProjectID=" + projectId,
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
                        setAdapter(3)
                        getPhaseList()
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
                        setAdapter(6)
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
                        setAdapter(4)
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
                        setAdapter(5)
                        getSubModuleListListValues(projectId,projectTaskId)
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

    fun setAdapter(id: Int) {
        if (id == 1) {
            priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityList)
            priorityAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.prioritySpinner.adapter = priorityAdapter
        } else if (id == 2) {
            taskStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskStatusList)
            taskStatusAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.taskStatusSpinner.adapter = taskStatusAdapter
        }
        else if (id == 3) {
            moduleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moduleNameList)
            moduleAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.moduleSpinner.adapter = moduleAdapter
        }
        else if (id == 4) {
            taskCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskCategoryNameList)
            taskCategoryAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.taskCategorySpinner.adapter = taskCategoryAdapter
        }
        else if (id == 5) {
            issueAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, issueNameList)
            issueAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.issueTypeSpinner.adapter = issueAdapter
        }
        else if (id == 6) {
            phaseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, phaseDescriptionList)
            phaseAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.phaseSpinner.adapter = phaseAdapter
        }
    }

    private fun getSubModuleListListValues(pId : Int?, mId : Int?)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.editSubModuleById + SharedPref.getCompanyId(this) + "&ProjectID=" + pId + "&PrjTaskID=" + mId,
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
                                subModuleTaskName = loginObject.getString("TaskName")
                                subModulePhaseDescription = loginObject.getInt("PhaseDescription")
                                subModuleTaskCategory = loginObject.getInt("TaskCategory")
                                subModuleTaskType = loginObject.getString("TaskType")
                                subModuleStartDate = loginObject.getString("EstStartDate")
                                subModuleEndDate = loginObject.getString("EstEndDate")
                                subModuleEstHours = loginObject.getString("EstTotalHours")
                                subModulePriority = loginObject.getString("Priority")
                                subModuleCardViewStatus = loginObject.getString("CardviewStatus")
                                subModuleIsActive = loginObject.getBoolean("Active")
                                subModuleIsBillable = loginObject.getBoolean("Billable")

                            }
                            getSpinnerIndex(binding!!.moduleSpinner, moduleIdList, subModuleId, true)
                            binding!!.screenNameValue.setText(subModuleTaskName)
                            getSpinnerIndex(binding!!.phaseSpinner, phaseIdList, subModulePhaseDescription, true)
                            getSpinnerIndex(binding!!.taskCategorySpinner, taskCategoryIdList, subModuleTaskCategory, true)
                            getSpinnerIndex(binding!!.issueTypeSpinner, issueIdList, Integer.parseInt(subModuleTaskType), true)
                            val separatedStartDate: Array<String> = subModuleStartDate!!.split("T").toTypedArray()
                            val separatedEndDate: Array<String> = subModuleEndDate!!.split("T").toTypedArray()
                            binding!!.startDate.setText(separatedStartDate[0])
                            binding!!.endDate.setText(separatedEndDate[0])
                            binding!!.estimatedHoursValue.setText(subModuleEstHours)

                            if(subModulePriority == "Low" || subModulePriority.equals("Low"))
                            {
                                binding!!.prioritySpinner.setSelection(1)
                            }
                            else if(subModulePriority == "Medium" || subModulePriority.equals("Medium"))
                            {
                                binding!!.prioritySpinner.setSelection(2)
                            }
                            else if(subModulePriority == "High" || subModulePriority.equals("High"))
                            {
                                binding!!.prioritySpinner.setSelection(3)
                            }
                            else if(subModulePriority == "Critical" || subModulePriority.equals("Critical"))
                            {
                                binding!!.prioritySpinner.setSelection(4)
                            }


                            if(subModuleCardViewStatus == "Unassigned" || subModuleCardViewStatus.equals("Unassigned"))
                            {
                                binding!!.taskStatusSpinner.setSelection(0)
                            }
                            else if(subModuleCardViewStatus == "Assigned" || subModuleCardViewStatus.equals("Assigned"))
                            {
                                binding!!.taskStatusSpinner.setSelection(1)
                            }
                            else if(subModuleCardViewStatus == "InProgress" || subModuleCardViewStatus.equals("InProgress"))
                            {
                                binding!!.taskStatusSpinner.setSelection(2)
                            }
                            else if(subModuleCardViewStatus == "Completed" || subModuleCardViewStatus.equals("Completed"))
                            {
                                binding!!.taskStatusSpinner.setSelection(3)
                            }

                            if(subModuleIsActive == true)
                            {
                                binding!!.isActive.isChecked = true
                            }

                            if(subModuleIsBillable == true)
                            {
                                binding!!.isBillable.isChecked = true
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

    private fun updateSubModule(moduleId: Int,taskName: String,taskCategoryId: Int,isActive: Boolean,isBillable: Boolean,startDate: String,taskType: Int,endDate: String,estHours :String,taskStatus: String,phaseValue: Int,priorityValue: String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.updateSubModule + SharedPref.getCompanyId(this) +"&ProjectID=" + projectId + "&UserName=" + SharedPref.getCompanyId(this) + "&PrjTaskID=" + projectTaskId + "&ModuleID=" + moduleId + "&TaskName=" + taskName + "&TaskCategory=" + taskCategoryId + "&TaskType=" + taskType + "&Active=" + isActive + "&Billable=" + isBillable + "&TicketNo=" + "&EstTotalHours=" + estHours + "&Priority=" + priorityValue +"&PhaseDescription=" + phaseValue + "&EstStartDate=" + startDate + "&EstEndDate=" + endDate,
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
                            if (errMsg.equals("Record Updated Successfully") || errMsg == "Record Updated Successfully") {
                                CommonMethod.showToast(this, "Module Updated Successfully..!")
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