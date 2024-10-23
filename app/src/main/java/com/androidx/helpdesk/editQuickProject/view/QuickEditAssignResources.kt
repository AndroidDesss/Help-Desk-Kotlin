package com.androidx.helpdesk.editQuickProject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditAssignResourcesBinding
import com.androidx.helpdesk.editQuickProject.adapter.EditAssignedResourceAdapter
import com.androidx.helpdesk.editQuickProject.adapter.EditAvailableResourceAdapter
import com.androidx.helpdesk.editQuickProject.model.EditAssignResourceModel
import com.androidx.helpdesk.editQuickProject.model.EditAvailableResourceModel
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.editQuickProject.`interface`.OnDeleteListener
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class QuickEditAssignResources : AppCompatActivity(), OnProgrammerListener, OnDeleteListener {

    private var binding: ActivityEditAssignResourcesBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var projectEmployeeId = 0

    private var availableEmployeeId = 0

    private var projectName: String? = null

    private var employeeName: String? = null

    private var employeeId: String? = null

    private var email: String? = null

    private var errMsg: String? = null

    private var availableEmployeeName: String? = null

    private var availableDepartment: String? = null

    private var availableDesignation: String? = null

    private var errorMsg: String? = null

    private var editAssignedResourceList: MutableList<EditAssignResourceModel> = ArrayList()

    private var editAssignedResourceAdapter: EditAssignedResourceAdapter? = null

    private var editAvailableResourceList: MutableList<EditAvailableResourceModel> = ArrayList()

    private var editAvailableResourceAdapter: EditAvailableResourceAdapter? = null

    private var employeeIdList = ArrayList<Int>()

    private var employeeIdDeleteList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_assign_resources)
        initListener()
        getBundleData()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.assignResources.setOnClickListener(onClickListener)
        binding!!.btnRemove.setOnClickListener(onClickListener)
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        getAssignedResources()
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.assignResources ->
            {
                getAvailableProgrammers(view)
            }
            R.id.btnRemove ->
            {
                if(employeeIdDeleteList.isEmpty() || employeeIdDeleteList==null)
                {
                    CommonMethod.showToast(this, "Please Select the Programmers..!")
                }
                else
                {
                    deleteResources(employeeIdDeleteList)
                }
            }
            R.id.btnNext ->
            {
                startActivity(Intent(this, QuickEditModule::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
            }
        }
    }

    private fun getAssignedResources()
    {
        CommonMethod.showProgressDialog(this)
        binding!!.rlError.visibility = View.GONE
        editAssignedResourceList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getAssignedProjectMembers + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                employeeName = loginObject.getString("EName")
                                employeeId = loginObject.getString("EmpID")
                                projectEmployeeId = loginObject.getInt("PrjEmpID")
                                email = loginObject.getString("Email")
                                editAssignedResourceList.add(EditAssignResourceModel(employeeName, employeeId,projectEmployeeId, email))
                            }
                            binding!!.btnContainer.visibility = View.VISIBLE
                            editAssignedResourceAdapter = EditAssignedResourceAdapter(this, editAssignedResourceList,this)
                            binding!!.recyclerView.adapter = editAssignedResourceAdapter
                            editAssignedResourceAdapter!!.notifyDataSetChanged()
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                        binding!!.btnContainer.visibility = View.GONE
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun getAvailableProgrammers(view: View)
    {
        CommonMethod.showProgressDialog(this)
        binding!!.rlError.visibility = View.GONE
        editAvailableResourceList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getAvailableProgrammers + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                availableDepartment = loginObject.getString("Department")
                                availableDesignation = loginObject.getString("Designation")
                                availableEmployeeName = loginObject.getString("EmpName")
                                availableEmployeeId = loginObject.getInt("EmpID")
                                editAvailableResourceList.add(EditAvailableResourceModel(availableDepartment, availableDesignation,availableEmployeeName, availableEmployeeId))
                            }
                            editAvailableResourceAdapter = EditAvailableResourceAdapter(this, editAvailableResourceList,this)
                            popUpLayout(view)
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun popUpLayout(view: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.available_resources_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpRecyclerView=popupView.findViewById(R.id.programmerRecyclerView) as RecyclerView
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddEmployee) as Button
        popUpRecyclerView.adapter = editAvailableResourceAdapter
        editAvailableResourceAdapter!!.notifyDataSetChanged()

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }

        popUpSaveBtn.setOnClickListener()
        {
            if(employeeIdList.isEmpty() || employeeIdList==null)
            {
                CommonMethod.showToast(this, "Please Select the Programmers..!")
            }
            else
            {
                popupWindow.dismiss()
                addProgrammers(employeeIdList)
            }
        }
    }

    override fun onProgrammerItemClicked(position: Int) {
        if (employeeIdList.contains(editAvailableResourceList[position].availableEmployeeId))
        {
            employeeIdList.remove(editAvailableResourceList[position].availableEmployeeId)
        }
        else
        {
            employeeIdList.add(editAvailableResourceList[position].availableEmployeeId)
        }
    }

    private fun addProgrammers(members: ArrayList<Int>)
    {
        val projectMembers = members.joinToString(",")

        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.POST, Api.addAvailableProgrammers + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId+"&EmpID="+projectMembers,
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
                                employeeIdList.clear()
                                getAssignedResources()
                            }
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
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun deleteResources(deleteMembers: ArrayList<Int>) {

        val projectDeleteMembers = deleteMembers.joinToString(",")
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteAssignResourcesById + projectDeleteMembers,
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
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
                            if (errorMsg.equals("Removed") || errorMsg == "Removed") {
                                getAssignedResources()
                                employeeIdDeleteList.clear()
                            }
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
            CommonMethod.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onDeleteItemClicked(position: Int) {
        if (employeeIdDeleteList.contains(editAssignedResourceList[position].projectEmplyoeeId))
        {
            employeeIdDeleteList.remove(editAssignedResourceList[position].projectEmplyoeeId)
        }
        else
        {
            employeeIdDeleteList.add(editAssignedResourceList[position].projectEmplyoeeId)
        }
    }
}