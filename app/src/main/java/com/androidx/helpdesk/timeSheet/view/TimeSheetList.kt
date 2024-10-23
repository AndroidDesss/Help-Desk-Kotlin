package com.androidx.helpdesk.timeSheet.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentTimeSheetListBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import com.androidx.helpdesk.timeSheet.adapter.TimeSheetAdapter
import com.androidx.helpdesk.timeSheet.model.TimeSheetModel
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimeSheetList : Fragment() {

    private var binding: FragmentTimeSheetListBinding? = null

    private var firstVisit = false

    private var stringRequest: StringRequest? = null

    private val projectIdList: MutableList<Int> = ArrayList()

    private val projectNameList: MutableList<String?> = ArrayList()

    private var status = 0

    private var timeSheetProjectId = 0

    private var timeSheetAllottedHours = 0

    private var projectId = 0

    private var projectSpinnerIdValue = 0

    private var projectName: String? = null

    private var projectSpinnerNameValue: String? = null

    private var timeSheetProjectName: String? = null

    private var timeSheetTaskName: String? = null

    private var timeSheetAllottedTo: String? = null

    private var timeSheetAssignedDate: String? = null

    private var taskDescription: String? = null

    private var timeSheetAdapter: TimeSheetAdapter? = null

    private var timeSheetModelList: MutableList<TimeSheetModel> = ArrayList()

    private var projectNameType: ArrayAdapter<*>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_sheet_list, container, false)
        val layoutManager = GridLayoutManager(context,2)
        binding!!.recyclerView.layoutManager = layoutManager
        firstVisit = true

        projectList()

        var calender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR,year)
            calender.set(Calendar.MONTH,month)
            calender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLabel(calender)

        }

        binding!!.filter.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding!!.projectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                    projectSpinnerIdValue = projectIdList[position]
                    projectSpinnerNameValue = projectNameList[position]
                    timeSheetModelList.clear()
                    getTimeSheetList(projectSpinnerIdValue, projectSpinnerNameValue,CommonMethod.Companion.getCurrentDate(context))
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
        return binding!!.getRoot()
    }

    private fun updateLabel(myCalender: Calendar) {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.US)
        getTimeSheetList(projectSpinnerIdValue, projectSpinnerNameValue,sdf.format(myCalender.time))
    }


    private fun projectList()
    {
            CommonMethod.showProgressDialog(context)
            projectIdList.clear()
            projectNameList.clear()
            stringRequest = object : StringRequest(Method.POST, Api.projectListByUser + SharedPref.getCompanyId(context) + "&EmpID=" + SharedPref.getEmployeeId(context),
                Response.Listener { ServerResponse ->
                    try {
                        val jsondata = JSONObject(ServerResponse)
                        status = jsondata.getInt("status")
                        if (status == 200) {
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
                            CommonMethod.cancelProgressDialog(context)
                            CommonMethod.Companion.showToast(context, "No data")
                        }
                    } catch (e: JSONException) {
                        CommonMethod.cancelProgressDialog(context)
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    CommonMethod.cancelProgressDialog(context)
                    stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                }
            ) {
                override fun getParams(): Map<String, String>? {
                    return HashMap()
                }
            }
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }



    fun getTimeSheetList(pId: Int, pName: String?,selectedDate: String?) {
        timeSheetModelList.clear()
        CommonMethod.showProgressDialog(context)
        binding!!.rlError.visibility = View.GONE
        stringRequest = StringRequest(Request.Method.GET, Api.getTimeSheetList + pName + "&ProjectId=" + pId + "&Emp_Id=" + SharedPref.getEmployeeId(context) + "&usertype_id=" + SharedPref.getUserId(context) + "&dept_id=" + SharedPref.getDepartmentId(context)+ "&date=" + selectedDate,
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(context)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                timeSheetProjectId = loginObject.getInt("PrjTaskID")
                                timeSheetProjectName = loginObject.getString("ProjectName")
                                timeSheetTaskName = loginObject.getString("TaskName")
                                timeSheetAllottedTo = loginObject.getString("FullName")
                                timeSheetAssignedDate = loginObject.getString("AllotedDate")
                                timeSheetAllottedHours = loginObject.getInt("AllotedHrs")
                                taskDescription = loginObject.getString("Notes")
                                timeSheetModelList.add(TimeSheetModel(timeSheetProjectId, timeSheetProjectName, timeSheetTaskName, timeSheetAllottedTo, timeSheetAssignedDate, timeSheetAllottedHours,taskDescription))
                            }
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                    }
                    timeSheetAdapter = TimeSheetAdapter(context, timeSheetModelList)
                    binding!!.recyclerView.adapter = timeSheetAdapter
                    timeSheetAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(context)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(context)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.Companion.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun setAdapter(id: Int)
    {
        if (id == 1)
        {
            projectNameType = ArrayAdapter(requireActivity(),R.layout.spinner_item, projectNameList)
            projectNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.projectSpinner.adapter = projectNameType
        }
    }



    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            projectList()
        }
    }


}