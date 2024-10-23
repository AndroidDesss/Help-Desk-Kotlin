package com.androidx.helpdesk.sprint.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditSprintScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class EditSprintScreen : AppCompatActivity() {

    private var binding: ActivityEditSprintScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var selectedProjectId = 0

    private var boardId = 0

    private var selectedBoardId = 0

    private var sprintId = 0

    private var detailsProjectId = 0

    private var detailsBoardId = 0

    private var projectName: String? = null

    private var boardName: String? = null

    private var errorMsg: String? = null

    private var detailsSprintName: String? = null

    private var detailsSprintStartDate: String? = null

    private var detailsSprintEndDate: String? = null

    private var detailsSprintDeliveryDate: String? = null

    private var detailsSprintEstimatedHours: String? = null

    private var detailsSprintCompletedHours: String? = null

    private var detailsSprintComments: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private val boardIdList: MutableList<Int> = ArrayList()

    private val boardNameList: MutableList<String?> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null

    private var boardType: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_sprint_screen)
        CommonMethod.showProgressDialog(this)
        bundleData()
        getSprintDetails(sprintId)
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

    private fun bundleData()
    {
        val intent = intent
        sprintId = intent.getIntExtra("sprintId", 0)
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
                    updateSprint()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
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
            boardType = ArrayAdapter(this, android.R.layout.simple_spinner_item, boardNameList)
            boardType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.boardNameSpinner.adapter = boardType
            getSpinnerIndex(binding!!.boardNameSpinner, boardIdList, detailsBoardId, false)
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
                editText.setText(StringBuilder().append(monthOfYear + 1).append("-").append(dayOfMonth.toString()).append("-").append(year))
            }, year, month, day)
        datePickerDialog.show()
    }

    fun getSprintDetails(id: Int) {
        stringRequest = object : StringRequest(Method.POST, Api.editSprint + SharedPref.getUserId(this) + "&BoardID&EmpID&SprintID=" + id, Response.Listener
        { ServerResponse ->
            try {
                val jsondata = JSONObject(ServerResponse)
                status = jsondata.getInt("status")
                if (status == 200) {
                    val dataArray = jsondata.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val loginObject = dataArray.getJSONObject(i)
                        detailsProjectId = loginObject.getInt("ProjectID")
                        detailsBoardId = loginObject.getInt("BoardID")
                        detailsSprintName = loginObject.getString("SprintName")
                        detailsSprintStartDate = loginObject.getString("SprintStartDate")
                        detailsSprintEndDate = loginObject.getString("SprintEndDate")
                        detailsSprintDeliveryDate = loginObject.getString("SprintDeliveryDate")
                        detailsSprintEstimatedHours = loginObject.getString("SprintEstimatedHours")
                        detailsSprintCompletedHours = loginObject.getString("SprintCompletedHours")
                        detailsSprintComments = loginObject.getString("Comments")
                    }

                    binding!!.etSprintName.setText(detailsSprintName)

                    val sprintStartDateArray = detailsSprintStartDate!!.split("T").toTypedArray()
                    binding!!.etSprintStartDate.setText(sprintStartDateArray[0])

                    val sprintEndDateArray = detailsSprintEndDate!!.split("T").toTypedArray()
                    binding!!.etSprintEndDate.setText(sprintEndDateArray[0])

                    val sprintDeliveryDateArray = detailsSprintDeliveryDate!!.split("T").toTypedArray()
                    binding!!.etSprintDeliveryDate.setText(sprintDeliveryDateArray[0])

                    binding!!.etSprintEstimatedHours.setText(detailsSprintEstimatedHours)
                    binding!!.etSprintCompleteHoursName.setText(detailsSprintCompletedHours)

                    if(detailsSprintComments != null)
                    {
                        binding!!.etCommentsName.setText(detailsSprintComments)
                    }
                    else
                    {
                        binding!!.etCommentsName.setText("")
                    }

                    projectList()

                }
                else
                {
                    CommonMethod.Companion.showToast(this, "No data")
                }
            } catch (e: JSONException) {
                CommonMethod.cancelProgressDialog(this)
                e.printStackTrace()
            }
        },
            Response.ErrorListener {
                CommonMethod.cancelProgressDialog(this)
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

    private fun updateSprint()
    {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(
            Request.Method.POST,
            Api.updateSprint + selectedProjectId  + "&BoardID=" + selectedBoardId + "&SprintID=" + sprintId + "&SprintName=" + binding!!.etSprintStartDate.text.toString() +  "&DeliveryDate=" + binding!!.etSprintDeliveryDate.text.toString() + "&EstimatedHours=" + binding!!.etSprintEstimatedHours.text.toString() + "&CompletedHours=" + binding!!.etSprintCompleteHoursName.text.toString() + "&Comments=" + binding!!.etCommentsName.text.toString() +"&EndDate="+ binding!!.etSprintEndDate.text.toString()+"&StartDate="+ binding!!.etSprintStartDate.text.toString(),
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
                        if (errorMsg.equals("Updated Successfully", ignoreCase = true)) {
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


}