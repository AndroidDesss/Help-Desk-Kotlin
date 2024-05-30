package com.androidx.helpdesk.board.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditBoardScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class EditBoardScreen : AppCompatActivity() {

    private var binding: ActivityEditBoardScreenBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var boardId = 0

    private var projectId = 0

    private var detailsProjectId = 0

    private var selectedProjectId = 0

    private var projectNameType: ArrayAdapter<*>? = null

    private var projectName: String? = null

    private var boardName: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_board_screen)
        getBundleData()
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

    private fun getBundleData() {
        val mIntent = intent
        boardId = mIntent.getIntExtra("boardId", 0)
        getBoardDetails()
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
                    updateBoardDetails()
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

    private fun getBoardDetails()
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.editBoardDetails + boardId,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                detailsProjectId = loginObject.getInt("ProjectID")
                                projectName = loginObject.getString("ProjectName")
                                boardName = loginObject.getString("BoardName")
                            }
                            projectList()
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

    private fun updateBoardDetails() {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.updateBoardDetails + boardId + "&ProjectID=" + selectedProjectId + "&BoardName="+ binding!!.etBoardName.text.toString(),
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        finish()
                        CommonMethod.showToast(this, "Board Updated Successfully")
                    } else {
                        CommonMethod.showToast(this, "Error")
                    }
                } catch (e: JSONException) {
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

    private fun projectList()
    {
        projectIdList.clear()
        projectNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogProjectList + SharedPref.getCompanyId(this)  + "&UserTypeID=" + SharedPref.getUserId(this) + "&Est&EmpID=" + SharedPref.getEmployeeId(this),
            { ServerResponse ->
                try {
                    binding!!.cardView.visibility = View.GONE
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
                        binding!!.etBoardName.setText(boardName)
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

    fun setAdapter()
    {
        projectNameType = ArrayAdapter(this, android.R.layout.simple_spinner_item, projectNameList)
        projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.projectNameSpinner.adapter = projectNameType
        getSpinnerIndex(binding!!.projectNameSpinner, projectIdList, detailsProjectId, false)
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