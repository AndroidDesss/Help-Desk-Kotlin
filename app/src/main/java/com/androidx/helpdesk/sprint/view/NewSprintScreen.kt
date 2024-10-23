package com.androidx.helpdesk.sprint.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityNewSprintScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class NewSprintScreen : AppCompatActivity() {

    private var binding: ActivityNewSprintScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var selectedProjectId = 0

    private var boardId = 0

    private var selectedBoardId = 0

    private var projectName: String? = null

    private var boardName: String? = null

    private var errorMsg: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private val boardIdList: MutableList<Int> = ArrayList()

    private val boardNameList: MutableList<String?> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null

    private var boardType: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_sprint_screen)
        CommonMethod.showProgressDialog(this)
        projectList()
        initListener()

        binding!!.projectNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    CommonMethod.showProgressDialog(parentView.context)
                    selectedProjectId = projectIdList[position]
                    getBoardType(selectedProjectId)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding!!.boardNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedBoardId = boardIdList[position]
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
        binding!!.etSprintStartDate.setOnClickListener(onClickListener)
        binding!!.etSprintEndDate.setOnClickListener(onClickListener)
        binding!!.etSprintDeliveryDate.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnSave -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this))
                {
                    CommonMethod.showProgressDialog(this)
                    createSprint()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
            R.id.etSprintStartDate ->
            {
                setDate(binding!!.etSprintStartDate)
            }
            R.id.etSprintEndDate ->
            {
                setDate(binding!!.etSprintEndDate)
            }
            R.id.etSprintDeliveryDate ->
            {
                setDate(binding!!.etSprintDeliveryDate)
            }
        }
    }

    private fun validateDetails(): Boolean {
        if (selectedProjectId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Project")
            return false
        }
        else if (binding!!.etSprintName.text.toString().isEmpty())
        {
            CommonMethod.Companion.showToast(this, "Sprint Name Required")
            return false
        }
        else if (selectedBoardId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Board Type")
            return false
        }
        else if (binding!!.etSprintStartDate.text.toString().isEmpty())
        {
            binding!!.etSprintStartDate.error = "Enter Sprint Start Date"
            return false
        }
        else if (binding!!.etSprintEndDate.text.toString().isEmpty())
        {
            CommonMethod.Companion.showToast(this, "Enter Sprint End Date")
            return false
        }
        else if (binding!!.etSprintDeliveryDate.text.toString().isEmpty())
        {
            CommonMethod.Companion.showToast(this, "Enter Sprint Delivery Date")
            return false
        }
        else if (binding!!.etSprintEstimatedHours.text.toString().isEmpty())
        {
            CommonMethod.Companion.showToast(this, "Enter Sprint Estimated Hours")
            return false
        }
        else if (binding!!.etSprintCompleteHoursName.text.toString().isEmpty())
        {
            CommonMethod.Companion.showToast(this, "Enter Sprint Completed Hours")
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
                        CommonMethod.cancelProgressDialog(this)
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
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
                CommonMethod.cancelProgressDialog(this)
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
                        setAdapter(2)
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
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
            boardType = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.boardNameSpinner.adapter = boardType
        }
    }

    private fun createSprint()
    {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(
            Request.Method.POST,
            Api.createSprint + selectedProjectId  + "&BoardID=" + selectedBoardId + "&SprintName=" + binding!!.etSprintStartDate.text.toString() + "&StartDate=" + binding!!.etSprintStartDate.text.toString() + "&DeliveryDate=" + binding!!.etSprintDeliveryDate.text.toString() + "&EstimatedHours=" + binding!!.etSprintEstimatedHours.text.toString() + "&CompletedHours=" + binding!!.etSprintCompleteHoursName.text.toString() + "&Comments=" + binding!!.etCommentsName.text.toString() +"&EndDate="+ binding!!.etSprintEndDate.text.toString(),
            { ServerResponse ->
                try {
                    CommonMethod.cancelProgressDialog(this)
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Inserted Successfully", ignoreCase = true)) {
                            CommonMethod.showToast(this, errorMsg)
                            finish()
                        }
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
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
                editText.setText(StringBuilder().append(monthOfYear + 1).append("-").append(dayOfMonth.toString()).append("-").append(year))
            }, year, month, day)
        datePickerDialog.show()
    }
}