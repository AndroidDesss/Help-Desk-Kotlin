package com.androidx.helpdesk.adminCardView.weeklyAllotment.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.weeklyAllotment.adapter.WeeklyTaskAllotmentAdapter
import com.androidx.helpdesk.adminCardView.weeklyAllotment.model.WeeklyTaskAllotmentModel
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentWeeklyAllotmentBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeeklyAllotmentFragment : Fragment() {

    private var binding: FragmentWeeklyAllotmentBinding? = null

    private var status = 0

    private var departmentId = 0

    private var projectTaskId = 0

    private var timeSheetId = 0

    private var departmentName: String? = null

    private var fullName: String? = null

    private var department: String? = null

    private var projectName: String? = null

    private var moduleName: String? = null

    private var taskName: String? = null

    private var allottedDate: String? = null

    private var taskStatus: String? = null

    private var allottedHours = 0

    private var workedHours = 0

    private var departmentNameType: ArrayAdapter<*>? = null

    private val departmentIdList: MutableList<Int> = ArrayList()

    private val departmentNameList: MutableList<String?> = ArrayList()

    private var stringRequest: StringRequest? = null

    private lateinit var formattedDates: List<String>

    private lateinit var weeklyAllotmentListUrl: String

    private var selectedDepartment = ""

    private var MY_SOCKET_TIMEOUT_MS = 300000

    private var weeklyAllotmentList: MutableList<WeeklyTaskAllotmentModel> =ArrayList()

    private var weeklyTaskAllotmentAdapter: WeeklyTaskAllotmentAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weekly_allotment, container, false)
        initListener()
        departmentList()
        binding!!.deportmentValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                try {
                    if (position == 0) {
                        selectedDepartment = "All"
                    } else {
                        selectedDepartment = departmentIdList[position].toString()
                    }
                } catch (e: Exception) {
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
        getCurrentDateWithUrl()
        return binding!!.root
    }

    private fun initListener() {
        binding!!.dateValue.setOnClickListener(onClickListener)
        binding!!.taskView.setOnClickListener(onClickListener)
    }

    private fun getCurrentDateWithUrl()
    {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val  formattedDate = dateFormat.format(currentDate.time)
        binding?.dateValue?.setText(formattedDate)


        val weekDates = getWeekDates(currentDate)
        formattedDates = weekDates.map { dateFormat.format(it.time) }

        weeklyAllotmentListUrl = Api.getWeeklyAllotmentList +
                "MondayDate=${formattedDates[0]}&" +
                "TuesdayDate=${formattedDates[1]}&" +
                "WednesdayDate=${formattedDates[2]}&" +
                "ThursdayDate=${formattedDates[3]}&" +
                "FridayDate=${formattedDates[4]}&" +
                "SaturdayDate=${formattedDates[5]}"
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.dateValue -> {
                setDate(binding!!.dateValue)
            }
            R.id.taskView ->{
                getWeeklyAllotmentList(weeklyAllotmentListUrl)
            }
        }
    }

    private fun setDate(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val weekDates = getWeekDates(selectedDate)
                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                formattedDates = weekDates.map { dateFormat.format(it.time) }

                //Assuming the week starts from Monday
                weeklyAllotmentListUrl = Api.getWeeklyAllotmentList +
                        "MondayDate=${formattedDates[0]}&" +
                        "TuesdayDate=${formattedDates[1]}&" +
                        "WednesdayDate=${formattedDates[2]}&" +
                        "ThursdayDate=${formattedDates[3]}&" +
                        "FridayDate=${formattedDates[4]}&" +
                        "SaturdayDate=${formattedDates[5]}"

                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDayOfMonth)
                editText.setText(StringBuilder().append(formattedMonth).append("-").append(formattedDay).append("-").append(year))

            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun getWeekDates(selectedDate: Calendar): List<Calendar> {
        val weekDates = mutableListOf<Calendar>()
        val dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
        val startDate = selectedDate.clone() as Calendar
        startDate.add(Calendar.DAY_OF_MONTH, Calendar.MONDAY - dayOfWeek)

        for (i in 0..6) {
            val day = startDate.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, i)
            weekDates.add(day)
        }
        return weekDates
    }

    private fun departmentList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        departmentIdList.clear()
        departmentNameList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getDeportment,
            { ServerResponse ->
                try {
                    binding!!.cardView.visibility = View.GONE
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        departmentNameList.add("All")
                        departmentIdList.add(0)
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            departmentId = loginObject.getInt("DeptID")
                            departmentName = loginObject.getString("Department")
                            departmentIdList.add(departmentId)
                            departmentNameList.add(departmentName)
                        }
                        setAdapter(1)
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
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun setAdapter(id: Int) {
        if (id == 1)
        {
            departmentNameType = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departmentNameList)
            departmentNameType!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.deportmentValue.adapter = departmentNameType
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getWeeklyAllotmentList(weeklyAllotmentListUrl: String?)
    {
        binding!!.cardView.visibility = View.VISIBLE
        weeklyAllotmentList.clear()
        stringRequest = StringRequest(Request.Method.GET, weeklyAllotmentListUrl + "&Dept=" + selectedDepartment,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try
                {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0)
                        {
                            for (i in 0 until dataArray.length())
                            {
                                val loginObject = dataArray.getJSONObject(i)
                                projectTaskId = loginObject.optInt("PrjTaskAllotID")
                                timeSheetId = loginObject.optInt("TimeSheetID")
                                department = loginObject.optString("Department")
                                fullName = loginObject.optString("FullName")
                                projectName = loginObject.optString("ProjectName")
                                moduleName = loginObject.optString("ModuleName")
                                taskName = loginObject.optString("TaskName")
                                allottedHours = loginObject.optInt("TimesheetAllotedHrs")
                                taskStatus = loginObject.optString("TaskStatus")
                                workedHours = loginObject.optInt("ActualWorkedHours")
                                allottedDate = loginObject.optString("AllotedDate")
                                val allottedTimeParts = allottedDate.toString().split("T")
                                val allottedDatePart = allottedTimeParts[0]
                                weeklyAllotmentList.add(WeeklyTaskAllotmentModel(projectTaskId,timeSheetId,department,fullName,projectName,moduleName,taskName,allottedHours,taskStatus,workedHours,allottedDatePart))
                            }
                        }
                    }
                    else
                    {
                        binding!!.rlError.visibility = View.VISIBLE
                    }
                    weeklyTaskAllotmentAdapter = WeeklyTaskAllotmentAdapter(context, weeklyAllotmentList)
                    binding!!.weeklyTaskRecyclerView.adapter = weeklyTaskAllotmentAdapter
                    weeklyTaskAllotmentAdapter!!.notifyDataSetChanged()
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