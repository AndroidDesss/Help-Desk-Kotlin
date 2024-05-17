package com.androidx.helpdesk.createTickets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.androidx.helpdesk.authentication.DashBoardActivity
import com.androidx.helpdesk.databinding.ActivityClientTicketCreationBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ClientTicketCreationActivity : AppCompatActivity() {

    private var binding: ActivityClientTicketCreationBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var moduleTaskId = 0

    private var projectIdValue = 0

    private var taskTypeIdValue = 0

    private var severityIdValue = 0

    private var projectName: String? = null

    private var taskName: String? = null

    private var emailList: String? = null

    private var errorMsg: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private val taskIdList: MutableList<Int> = ArrayList()

    private val taskNameList: MutableList<String?> = ArrayList()

    var severityList = arrayOf<String?>("Critical", "High", "Medium", "Low")

    private var projectNameType: ArrayAdapter<*>? = null

    private var taskType: ArrayAdapter<*>? = null

    private var severityType: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_ticket_creation)
        initListener()
        binding!!.cardView.visibility = View.VISIBLE
        setAdapter(3)
        projectList()
        taskTypeList()
        binding!!.projectSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                    if (parentView.getItemAtPosition(position) == "Select") {
                        emailList = ""
                    } else {
                        emailList = ""
                        projectIdValue = projectIdList[position]
                        getEmail(projectIdValue)
                    }
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
        binding!!.taskTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                    taskTypeIdValue = taskIdList[position]
                }
                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnSave -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this)) {
                    createTask()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean {
        if (binding!!.etSubject.text.toString() == null || binding!!.etSubject.text.toString().isEmpty())
        {
            binding!!.etSubject.error = "Enter Subject"
            return false
        }
        else if (binding!!.tinNotes.text.toString() == null || binding!!.tinNotes.text.toString().isEmpty())
        {
            binding!!.tinNotes.error = "Enter Notes"
            return false
        }
        else if (binding!!.tinBody.text.toString() == null || binding!!.tinBody.text.toString().isEmpty())
        {
            binding!!.tinBody.error = "Enter Description"
            return false
        }
        else if (projectIdValue == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Project")
            return false
        }
        else if (taskTypeIdValue == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Task Type")
            return false
        }
        else
        {
            return true
        }
    }

    private fun projectList()
    {
            projectIdList.clear()
            projectNameList.clear()
            stringRequest = StringRequest(
                Request.Method.GET, Api.projectList + SharedPref.getCompanyId(this) + "&ClientID=" + SharedPref.getClientId(this) + "&UserTypeID=" + SharedPref.getUserId(this) + "&EmpID=" + SharedPref.getEmployeeId(this),
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
                                setAdapter(1)
                            }
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

    private fun taskTypeList() {
            taskIdList.clear()
            taskNameList.clear()
            stringRequest = StringRequest(
                Request.Method.GET, Api.taskTypeList,
                { ServerResponse ->
                    binding!!.cardView.visibility = View.GONE
                    try {
                        val jsondata = JSONObject(ServerResponse)
                        status = jsondata.getInt("status")
                        if (status == 200) {
                            taskNameList.add("Select")
                            taskIdList.add(0)
                            val dataArray = jsondata.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                moduleTaskId = loginObject.getInt("ModuleTypeID")
                                taskName = loginObject.getString("ModuleTypeName")
                                taskIdList.add(moduleTaskId)
                                taskNameList.add(taskName)
                                setAdapter(2)
                            }
                        }
                        else
                        {
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

    fun getEmail(projectIdForEmail: Int) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.GET, Api.emailList + projectIdForEmail,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            emailList = loginObject.getString("EmailToCommunicate")
                        }
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
            projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
            projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.projectSpinner.adapter = projectNameType
        } else if (id == 2) {
            taskType = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskNameList)
            taskType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.taskTypeSpinner.adapter = taskType
        } else if (id == 3) {
            severityType = ArrayAdapter(this, android.R.layout.simple_spinner_item, severityList)
            severityType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.severitySpinner.adapter = severityType
        }
    }

    fun createTask()
    {
        val value = binding!!.severitySpinner.selectedItem.toString()
        if (value.equals("Critical") || value == "Critical") {
            severityIdValue = 1
        } else if (value.equals("High") || value == "High") {
            severityIdValue = 2
        } else if (value.equals("Medium") || value == "Medium") {
            severityIdValue = 3
        } else if (value.equals("Low") || value == "Low") {
            severityIdValue = 4
        }
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.createTaskForClient + SharedPref.getEmployeeId(this) + "&ToEmail=" + emailList + "&ProjectID=" + projectIdValue + "&Subject=" + binding!!.etSubject.text.toString() + "&HTMLBody=" + binding!!.tinBody.text.toString() + "&Body=" + binding!!.tinNotes.text.toString() + "&TicketSeverity=" + severityIdValue + "&TaskType=" + taskTypeIdValue + "&ClientName=" + SharedPref.getClientName(this),
            { ServerResponse ->
                binding!!.cardView.visibility = View.VISIBLE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Record Updated") || errorMsg == "Record Updated") {
                            CommonMethod.Companion.showToast(this, errorMsg)
                            startActivity(Intent(this, DashBoardActivity::class.java))
                            finish()
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, " Wrong")
                    }
                } catch (e: JSONException) {
                    CommonMethod.Companion.showToast(this, "Something went Wrong")
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.Companion.showToast(this, "Error")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}