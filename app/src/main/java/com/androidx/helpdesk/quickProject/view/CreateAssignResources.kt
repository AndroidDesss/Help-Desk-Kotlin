package com.androidx.helpdesk.quickProject.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityCreateAssignResourcesBinding
import com.androidx.helpdesk.quickProject.`interface`.OnDeleteListener
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.quickProject.adapter.AssignedResourceAdapter
import com.androidx.helpdesk.quickProject.adapter.AvailableResourceAdapter
import com.androidx.helpdesk.quickProject.model.AssignResourceModel
import com.androidx.helpdesk.quickProject.model.AvailableResourceModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class CreateAssignResources : AppCompatActivity(), OnProgrammerListener,OnDeleteListener {

    private var binding:ActivityCreateAssignResourcesBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var projectEmployeeId = 0

    private var availableEmployeeId = 0

    private var errMsg: String? = null

    private var employeeName: String? = null

    private var projectName: String? = null

    private var employeeId: String? = null

    private var availableEmployeeName: String? = null

    private var availableDepartment: String? = null

    private var availableDesignation: String? = null

    private var email: String? = null

    private var errorMsg: String? = null

    private var assignedResourceList: MutableList<AssignResourceModel> = ArrayList()

    private var assignedResourceAdapter: AssignedResourceAdapter? = null

    private var availableResourceList: MutableList<AvailableResourceModel> = ArrayList()

    private var availableResourceAdapter: AvailableResourceAdapter? = null

    private var employeeIdList = ArrayList<Int>()

    private var employeeIdDeleteList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_assign_resources)
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
                startActivity(Intent(this, CreateModule::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
            }
        }
    }

    private fun getAssignedResources()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        assignedResourceList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getAssignedProjectMembers + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
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
                                employeeName = loginObject.getString("EName")
                                employeeId = loginObject.getString("EmpID")
                                projectEmployeeId = loginObject.getInt("PrjEmpID")
                                email = loginObject.getString("Email")
                                assignedResourceList.add(AssignResourceModel(employeeName, employeeId,projectEmployeeId, email))
                            }
                            binding!!.btnContainer.visibility = View.VISIBLE
                            assignedResourceAdapter = AssignedResourceAdapter(this, assignedResourceList,this)
                            binding!!.recyclerView.adapter = assignedResourceAdapter
                            assignedResourceAdapter!!.notifyDataSetChanged()
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                        binding!!.btnContainer.visibility = View.GONE
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

    private fun getAvailableProgrammers(view: View)
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        availableResourceList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.getAvailableProgrammers + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
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
                                availableDepartment = loginObject.getString("Department")
                                availableDesignation = loginObject.getString("Designation")
                                availableEmployeeName = loginObject.getString("EmpName")
                                availableEmployeeId = loginObject.getInt("EmpID")
                                availableResourceList.add(AvailableResourceModel(availableDepartment, availableDesignation,availableEmployeeName, availableEmployeeId))
                            }
                            availableResourceAdapter = AvailableResourceAdapter(this, availableResourceList,this)
                            popUpLayout(view)
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
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
        val popupView = inflater.inflate(R.layout.available_resources_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpRecyclerView=popupView.findViewById(R.id.programmerRecyclerView) as RecyclerView
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddEmployee) as Button
        popUpRecyclerView.adapter = availableResourceAdapter
        availableResourceAdapter!!.notifyDataSetChanged()

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
        if (employeeIdList.contains(availableResourceList[position].availableEmployeeId))
        {
            employeeIdList.remove(availableResourceList[position].availableEmployeeId)
        }
        else
        {
            employeeIdList.add(availableResourceList[position].availableEmployeeId)
        }
    }


    private fun addProgrammers(members: ArrayList<Int>)
    {
        val projectMembers = members.joinToString(",")

        binding!!.cardView.visibility = View.VISIBLE
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

    private fun deleteResources(deleteMembers: ArrayList<Int>) {

        val projectDeleteMembers = deleteMembers.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteAssignResourcesById + projectDeleteMembers,
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
                            if (errorMsg.equals("Removed") || errorMsg == "Removed") {
                                getAssignedResources()
                                employeeIdDeleteList.clear()
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

    override fun onDeleteItemClicked(position: Int) {
        if (employeeIdDeleteList.contains(assignedResourceList[position].projectEmplyoeeId))
        {
            employeeIdDeleteList.remove(assignedResourceList[position].projectEmplyoeeId)
        }
        else
        {
            employeeIdDeleteList.add(assignedResourceList[position].projectEmplyoeeId)
        }
    }

}