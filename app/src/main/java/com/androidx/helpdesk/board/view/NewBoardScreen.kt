package com.androidx.helpdesk.board.view

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
import com.androidx.helpdesk.databinding.ActivityNewBoardScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class NewBoardScreen : AppCompatActivity() {

    private var binding: ActivityNewBoardScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var selectedProjectId = 0

    private var projectId = 0

    private var projectName: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_board_screen)
        projectList()
        initListener()

        binding!!.projectNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                if (parentView.getItemAtPosition(position) != "Select")
                {
                    selectedProjectId = projectIdList[position]
                }
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
                    insertBoardDetails()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean {
        if (selectedProjectId == 0)
        {
            CommonMethod.Companion.showToast(this, "Select Project")
            return false
        }
        else if (binding!!.etBoardName.text.toString() == null || binding!!.etBoardName.text.toString().isEmpty())
        {
            binding!!.etBoardName.error = "Enter Board Name"
            return false
        }
        else {
            return true
        }
    }

    private fun insertBoardDetails() {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(
            Request.Method.POST, Api.insertNewBoard + selectedProjectId  + "&BoardName="+ binding!!.etBoardName.text.toString(),
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        finish()
                        CommonMethod.showToast(this, "Board Inserted Successfully")
                    }
                } catch (e: JSONException) {
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

    private fun projectList()
    {
        CommonMethod.showProgressDialog(this)
        projectIdList.clear()
        projectNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogProjectList + SharedPref.getCompanyId(this)  + "&UserTypeID=" + SharedPref.getUserId(this) + "&Est&EmpID=" + SharedPref.getEmployeeId(this),
            { ServerResponse ->
                try {
                    CommonMethod.cancelProgressDialog(this)
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
                        setAdapter()
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

    fun setAdapter()
    {
        projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
        projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.projectNameSpinner.adapter = projectNameType
    }
}