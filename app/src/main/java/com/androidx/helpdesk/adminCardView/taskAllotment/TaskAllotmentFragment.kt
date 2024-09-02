package com.androidx.helpdesk.adminCardView.taskAllotment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminTimeSheetTask.adapter.HaveTaskAdapter
import com.androidx.helpdesk.adminTimeSheetTask.adapter.HourTaskAdapter
import com.androidx.helpdesk.adminTimeSheetTask.adapter.NoTaskAdapter
import com.androidx.helpdesk.adminTimeSheetTask.model.HaveTaskModel
import com.androidx.helpdesk.adminTimeSheetTask.model.HourTaskModel
import com.androidx.helpdesk.adminTimeSheetTask.model.NoTaskModel
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentTaskAllotmentBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskAllotmentFragment : Fragment() {

    private var binding: FragmentTaskAllotmentBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var noTaskModelList: MutableList<NoTaskModel> =ArrayList()

    private var haveTaskModelList: MutableList<HaveTaskModel> =ArrayList()

    private var lessHourTaskModelList: MutableList<HourTaskModel> =ArrayList()

    private var empName: String? = null

    private var projectName: String? = null

    private var moduleName: String? = null

    private var taskName: String? = null

    private var taskStatus: String? = null

    private var allottedHours: Int? = null

    private var allottedDate: String? = null

    private var noTaskAdapter: NoTaskAdapter? = null

    private var lessHourTaskAdapter: HourTaskAdapter? = null

    private var haveTaskAdapter: HaveTaskAdapter? = null

    private var MY_SOCKET_TIMEOUT_MS = 300000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_allotment, container, false)
        initListener()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val  formattedDate = dateFormat.format(calendar.time)
        binding?.dateValue?.setText(formattedDate)
        getNoTaskDetails(formattedDate)
        return binding!!.root
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
                val formattedDate = binding!!.dateValue.text.toString()
                getNoTaskDetails(formattedDate)
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
            { _, year, monthOfYear, dayOfMonth ->
                val formattedMonth = String.format("%02d", monthOfYear + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                editText.setText(StringBuilder().append(formattedMonth).append("-").append(formattedDay).append("-").append(year))
            }, year, month, day
        )
        datePickerDialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getNoTaskDetails(formattedDate:String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        noTaskModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getNoTaskDetails +formattedDate +"&DeptID=",
            { ServerResponse ->
                try {

                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200)
                    {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length())
                        {
                            val loginObject = dataArray.getJSONObject(i)
                            empName = loginObject.getString("FullName")
                            noTaskModelList.add(NoTaskModel(empName))
                        }
                        binding!!.noTaskRecycler.visibility = View.VISIBLE
                        binding!!.noTaskEmpty.visibility = View.GONE
                    }
                    else if (status == 400)
                    {
                        binding!!.noTaskEmpty.visibility = View.VISIBLE
                        binding!!.noTaskRecycler.visibility = View.GONE
                    }
                    noTaskAdapter = NoTaskAdapter(requireContext(), noTaskModelList)
                    val layoutManager = GridLayoutManager(requireContext(), 2)
                    binding!!.noTaskRecycler.layoutManager = layoutManager
                    binding!!.noTaskRecycler.adapter = noTaskAdapter
                    noTaskAdapter!!.notifyDataSetChanged()
                    hourTaskValue(formattedDate)
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
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
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hourTaskValue(formattedDate:String){
        lessHourTaskModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getHourTaskDetails +formattedDate +"&DeptID=&projectId=",
            { ServerResponse ->
                try {

                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {

                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            empName = loginObject.getString("FullName")
                            projectName = loginObject.getString("ProjectName")
                            moduleName = loginObject.getString("ModuleName")
                            taskName = loginObject.getString("TaskName")
                            taskStatus = loginObject.getString("TaskStatus")
                            allottedDate = loginObject.getString("TaskDate")
                            allottedHours = loginObject.getInt("AllotedHrs")
                            val dateTimeParts = allottedDate.toString().split("T")
                            val datePart = dateTimeParts[0]
                            lessHourTaskModelList.add(HourTaskModel(empName,projectName,moduleName,taskName,taskStatus,datePart,allottedHours))
                        }
                        binding!!.lessHourRecycler.visibility = View.VISIBLE
                        binding!!.hourTaskEmpty.visibility = View.GONE

                    }
                    else if (status == 400)
                    {
                        binding!!.hourTaskEmpty.visibility = View.VISIBLE
                        binding!!.lessHourRecycler.visibility = View.GONE
                    }
                    lessHourTaskAdapter = HourTaskAdapter(requireContext(), lessHourTaskModelList)
                    binding!!.lessHourRecycler.adapter = lessHourTaskAdapter
                    lessHourTaskAdapter!!.notifyDataSetChanged()
                    haveTaskValue(formattedDate)
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
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
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun haveTaskValue(formattedDate:String)
    {
        haveTaskModelList.clear()
        stringRequest = StringRequest(Request.Method.GET, Api.getHaveTaskDetails +formattedDate +"&DeptID=&projectId=",
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200)
                    {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            empName = loginObject.getString("FullName")
                            projectName = loginObject.getString("ProjectName")
                            moduleName = loginObject.getString("ModuleName")
                            taskName = loginObject.getString("TaskName")
                            taskStatus = loginObject.getString("TaskStatus")
                            allottedDate = loginObject.getString("TaskDate")
                            allottedHours = loginObject.getInt("AllotedHrs")
                            val dateTimeParts = allottedDate.toString().split("T")
                            val datePart = dateTimeParts[0]
                            haveTaskModelList.add(HaveTaskModel(empName,projectName,moduleName,taskName,taskStatus,datePart,allottedHours))
                        }
                        binding!!.haveTaskRecycler.visibility = View.VISIBLE
                        binding!!.haveTaskEmpty.visibility = View.GONE
                    }
                    else if (status == 400)
                    {
                        binding!!.haveTaskEmpty.visibility = View.VISIBLE
                        binding!!.haveTaskRecycler.visibility = View.GONE
                    }
                    haveTaskAdapter = HaveTaskAdapter(requireContext(), haveTaskModelList)
                    binding!!.haveTaskRecycler.adapter = haveTaskAdapter
                    haveTaskAdapter!!.notifyDataSetChanged()
                    binding!!.cardView.visibility = View.GONE
                } catch (e: JSONException) {
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
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
        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }
}