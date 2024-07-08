package com.androidx.helpdesk.adminCardView.timesheet.view

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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheet.adapter.NotUpdatedTimesheetAdapter
import com.androidx.helpdesk.adminCardView.timesheet.adapter.UpdatedTimeSheetAdapter
import com.androidx.helpdesk.adminCardView.timesheet.model.NotUpdatedTimeSheetModel
import com.androidx.helpdesk.adminCardView.timesheet.model.UpdatedTimeSheetModel
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.FragmentTimeSheetBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeSheetFragment : Fragment() {

    private var binding: FragmentTimeSheetBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var timeSheetId = 0

    private var allottedHours = 0

    private var workingHours = 0

    private var empName: String? = null

    private var projectName: String? = null

    private var moduleName: String? = null

    private var taskName: String? = null

    private var taskStatus: String? = null

    private var notes: String? = null

    private var notUpdatedTimeSheetModelList: MutableList<NotUpdatedTimeSheetModel> =ArrayList()

    private var updatedTimeSheetModelList: MutableList<UpdatedTimeSheetModel> =ArrayList()

    private var notUpdatedTimeSheetAdapter: NotUpdatedTimesheetAdapter? = null

    private var updatedTimeSheetAdapter: UpdatedTimeSheetAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_time_sheet, container, false)
        initListener()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val  formattedDate = dateFormat.format(calendar.time)
        binding?.dateValue?.setText(formattedDate)
        getNotUpdatedTimesheetDetails(formattedDate)
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
                getNotUpdatedTimesheetDetails(formattedDate)
            }
        }
    }

    private fun getNotUpdatedTimesheetDetails(formattedDate:String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        notUpdatedTimeSheetModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getNotUpdatedTimeSheetDetails +formattedDate,
            { ServerResponse ->
                try {

                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {

                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            empName = loginObject.getString("FullName")
                            notUpdatedTimeSheetModelList.add(NotUpdatedTimeSheetModel(empName))
                        }
                        notUpdatedTimeSheetAdapter = NotUpdatedTimesheetAdapter(requireContext(), notUpdatedTimeSheetModelList)
                        val layoutManager = GridLayoutManager(requireContext(), 2)
                        binding!!.notUpdatedRecyclerView.layoutManager = layoutManager
                        binding!!.notUpdatedRecyclerView.adapter = notUpdatedTimeSheetAdapter
                        notUpdatedTimeSheetAdapter!!.notifyDataSetChanged()
                        binding!!.notUpdatedEmpty.visibility = View.GONE
                        getUpdatedTimeSheetDetails(formattedDate)
                    }
                    else if (status == 400)
                    {
                        binding!!.notUpdatedEmpty.visibility = View.VISIBLE
                        getUpdatedTimeSheetDetails(formattedDate)
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

    private fun getUpdatedTimeSheetDetails(formattedDate:String){
        updatedTimeSheetModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.updatedTimeSheetDetails +formattedDate ,
            { ServerResponse ->
                try {

                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        binding!!.cardView.visibility = View.GONE
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            timeSheetId = loginObject.getInt("TimeSheetID")
                            projectName = loginObject.getString("ProjectName")
                            moduleName = loginObject.getString("ProjectName")
                            taskName = loginObject.getString("TaskName")
                            allottedHours = loginObject.getInt("TimesheetAllotedHrs")
                            workingHours = loginObject.getInt("ActualWorkedHours")
                            taskStatus = loginObject.getString("TaskStatus")
                            notes = loginObject.getString("Notes")
                            updatedTimeSheetModelList.add(UpdatedTimeSheetModel(timeSheetId,projectName,moduleName,taskName,allottedHours,workingHours,taskStatus,notes))
                        }
                        updatedTimeSheetAdapter = UpdatedTimeSheetAdapter(requireContext(), updatedTimeSheetModelList)
                        val layoutManager = GridLayoutManager(requireContext(), 1)
                        binding!!.updatedRecyclerView.layoutManager = layoutManager
                        binding!!.updatedRecyclerView.adapter = updatedTimeSheetAdapter
                        updatedTimeSheetAdapter!!.notifyDataSetChanged()
                        binding!!.updatedEmpty.visibility = View.GONE
                    }
                    else if (status == 400)
                    {
                        binding!!.updatedEmpty.visibility = View.VISIBLE
                        binding!!.cardView.visibility = View.GONE
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

    private fun setDate(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                editText.setText(StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth).append("/").append(year))
            }, year, month, day
        )
        datePickerDialog.show()
    }
}