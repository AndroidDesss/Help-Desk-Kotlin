package com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.adapter.TaskAllotmentTimeSheetAdapter
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model.EmployeeTaskAllotmentModel
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model.TaskAllotmentTimeSheetModel
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentTimeSheetTaskAllotmentBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeSheetTaskAllotmentFragment : Fragment() {

    private var binding: FragmentTimeSheetTaskAllotmentBinding? = null

    private var stringRequest: StringRequest? = null

    private var MY_SOCKET_TIMEOUT_MS = 300000

    private var status = 0

    val taskAllotmentWeeklyAllotmentList = mutableListOf<EmployeeTaskAllotmentModel>()

    private var taskAllotmentTimeSheetAdapter: TaskAllotmentTimeSheetAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_sheet_task_allotment, container, false)
        getCurrentDateValues()
        initListener()
        return binding!!.root
    }

    private fun getCurrentDateValues()
    {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val  formattedDate = dateFormat.format(currentDate.time)
        binding?.dateValue?.setText(formattedDate)
        getTaskAllotmentTimesheetList(binding!!.dateValue.text.toString())
    }

    private fun initListener() {
        binding!!.dateValue.setOnClickListener(onClickListener)
        binding!!.taskView.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.dateValue -> {
                setDate(binding!!.dateValue)
            }
            R.id.taskView ->{
                getTaskAllotmentTimesheetList(binding!!.dateValue.text.toString())
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
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth ->
                editText.setText(StringBuilder().append(year).append("-").append(monthOfYear + 1).append("-").append(dayOfMonth.toString()))
            }, year, month, day)
        datePickerDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getTaskAllotmentTimesheetList(currentDate: String?)
    {
        binding!!.cardView.visibility = View.VISIBLE
        taskAllotmentWeeklyAllotmentList.clear()
        stringRequest = StringRequest(
            Request.Method.GET, Api.taskAllotmentTimeSheetList + currentDate,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val employeeObject = dataArray.getJSONObject(i)
                                val empId = employeeObject.optInt("EmpID")
                                val fullName = employeeObject.optString("FullName")
                                val depId = employeeObject.optInt("DeptID")
                                val departmentName = employeeObject.optString("Department")

                                val departmentArray = employeeObject.getJSONArray("DepartmentData")
                                val departmentList = mutableListOf<TaskAllotmentTimeSheetModel>()

                                for (j in 0 until departmentArray.length()) {
                                    val departmentObject = departmentArray.getJSONObject(j)
                                    departmentList.add(
                                        TaskAllotmentTimeSheetModel(
                                            depId = departmentObject.optInt("DeptID"),
                                            department = departmentObject.optString("Department"),
                                            taskRowCount = departmentObject.optInt("TaskRowCount"),
                                            timesheetRowCount = departmentObject.optInt("TimesheetRowCount"),
                                            projectTaskId = departmentObject.optInt("PrjTaskAllotID"),
                                            timeSheetId = departmentObject.optInt("TimeSheetID"),
                                            projectName = departmentObject.optString("ProjectName"),
                                            moduleName = departmentObject.optString("ModuleName"),
                                            taskName = departmentObject.optString("TaskName"),
                                            taskStatus = departmentObject.optString("TaskStatus"),
                                            allottedHours = departmentObject.optInt("AllotedHrs"),
                                            timesheetAllottedHours = departmentObject.optInt("TimesheetAllotedHrs"),
                                            actualWorkedHours = departmentObject.optInt("ActualWorkedHours")
                                        )
                                    )
                                }
                                taskAllotmentWeeklyAllotmentList.add(EmployeeTaskAllotmentModel(empId = empId, fullName = fullName,depId = depId, department = departmentName,departmentData = departmentList)
                                )
                            }
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                    }

                    taskAllotmentTimeSheetAdapter = TaskAllotmentTimeSheetAdapter(context, taskAllotmentWeeklyAllotmentList)
                    binding!!.weeklyTaskRecyclerView.adapter = taskAllotmentTimeSheetAdapter
                    taskAllotmentTimeSheetAdapter!!.notifyDataSetChanged()
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
}