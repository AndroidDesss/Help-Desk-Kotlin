package com.androidx.helpdesk.adminCardView.unAssigned.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.unAssigned.adapter.UnAssignedAdapter
import com.androidx.helpdesk.adminCardView.unAssigned.model.UnassignedModel
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentUnAssignedBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class UnAssignedFragment : Fragment() {

    private var binding: FragmentUnAssignedBinding? = null

    private var stringRequest: StringRequest? = null

    private var firstVisit = false

    private var status = 0

    private var projectTaskId = 0

    private var projectId = 0

    private var selectedProjectId = 0

    private var selectedProjectName: String? = null

    private var spinnerProjectName: String? = null

    private var projectName: String? = null

    private var moduleName: String? = null

    private var taskName: String? = null

    private var createdBy: String? = null

    private var createdDate: String? = null

    private var estCompletedDate: String? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private var unAssignedSummaryModelList: MutableList<UnassignedModel> = ArrayList()

    private var unAssignedSummaryAdapter: UnAssignedAdapter? = null

    private var projectNameType: ArrayAdapter<*>? = null

    private var MY_SOCKET_TIMEOUT_MS = 300000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_un_assigned, container, false)
        firstVisit = true
        projectList()
        binding!!.projectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long)
            {
                selectedProjectId = projectIdList[position]
                selectedProjectName = projectNameList[position].toString()
                getUnAssignedList(selectedProjectName!!,selectedProjectId)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            projectList()
        }
    }

    private fun projectList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        projectIdList.clear()
        projectNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getBackLogProjectList + SharedPref.getCompanyId(context)  + "&UserTypeID=" + SharedPref.getUserId(context) + "&Est&EmpID=" + SharedPref.getEmployeeId(context),
            { ServerResponse ->
                try {
                    binding!!.cardView.visibility = View.GONE
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        projectNameList.add("ALL")
                        projectIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            projectId = loginObject.getInt("ProjectID")
                            spinnerProjectName = loginObject.getString("ProjectName")
                            projectIdList.add(projectId)
                            projectNameList.add(spinnerProjectName)
                        }
                        setAdapter()
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
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUnAssignedList(selectedProjectName: String,selectedProjectId: Int)
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        unAssignedSummaryModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET, Api.getUnAssignedSummary + selectedProjectName + "&user_projectid=" + selectedProjectId + "&EmpID=" + SharedPref.getEmployeeId(context) + "&UserTypeID=" + SharedPref.getUserId(context) + "&DeptID=" + SharedPref.getDepartmentId(context),
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
                                projectTaskId = loginObject.getInt("PrjTaskID")
                                projectName = loginObject.getString("ProjectName")
                                moduleName = loginObject.getString("ModuleName")
                                taskName = loginObject.getString("TaskName")
                                createdBy = loginObject.getString("CreatedBy")
                                createdDate = loginObject.getString("CreatedDate")
                                val createdDateTimeParts = createdDate.toString().split("T")
                                val createdDatePart = createdDateTimeParts[0]
                                estCompletedDate = loginObject.getString("Est_CompletedDate")
                                val estCompletedDatePart = if (estCompletedDate == null || estCompletedDate!!.isEmpty()) {
                                    "undefined"
                                } else {
                                    val dateTimeParts = estCompletedDate!!.split("T")
                                    dateTimeParts[0]
                                }
                                unAssignedSummaryModelList.add(UnassignedModel(projectTaskId,projectName, moduleName,taskName,createdBy,createdDatePart,estCompletedDatePart))
                            }
                            unAssignedSummaryAdapter = UnAssignedAdapter(context, unAssignedSummaryModelList)
                            binding!!.recyclerView.adapter = unAssignedSummaryAdapter
                            unAssignedSummaryAdapter!!.notifyDataSetChanged()
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
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        stringRequest!!.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return MY_SOCKET_TIMEOUT_MS
            }

            override fun getCurrentRetryCount(): Int {
                return MY_SOCKET_TIMEOUT_MS
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    fun setAdapter()
    {
        projectNameType = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, projectNameList)
        projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.projectSpinner.adapter = projectNameType
    }
}