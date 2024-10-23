package com.androidx.helpdesk.editQuickProject.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityBackLogTaskTimesheetBinding
import com.androidx.helpdesk.editQuickProject.adapter.BackLogTaskTimeSheetAdapter
import com.androidx.helpdesk.editQuickProject.model.BackLogTaskTimeSheetModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class BackLogTaskTimesheet : AppCompatActivity() {

    private var binding: ActivityBackLogTaskTimesheetBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectTaskId = 0

    private var taskDate: String? = null

    private var taskName: String? = null

    private var taskStatus: String? = null

    private var actualWorkedHours:Double = 0.0

    private var allottedHours:Double = 0.0

    private var backLogTaskTimeSheetAdapter: BackLogTaskTimeSheetAdapter? = null

    private var backLogTaskTimesheetList: MutableList<BackLogTaskTimeSheetModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_back_log_task_timesheet)
        getBundleData()
        initListener()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
        }
    }

    private fun getBundleData() {
        val mIntent = intent
        projectTaskId = mIntent.getIntExtra("projectTaskId", 0)
        getBackLogTaskTimesheetList(projectTaskId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBackLogTaskTimesheetList(projectTaskId: Int)
    {
        CommonMethod.showProgressDialog(this)
        binding!!.rlError.visibility = View.GONE
        backLogTaskTimesheetList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.backLogTaskTimeSheetList + projectTaskId,
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
                                taskName = loginObject.optString("TaskName")
                                taskDate = loginObject.optString("TaskDate")
                                allottedHours = loginObject.optDouble("AllotedHrs")
                                actualWorkedHours = loginObject.optDouble("ActualWorkedHours")
                                taskStatus = loginObject.optString("TaskStausDes")
                                backLogTaskTimesheetList.add(BackLogTaskTimeSheetModel(taskName, taskDate,allottedHours, actualWorkedHours,taskStatus))
                            }
                            backLogTaskTimeSheetAdapter = BackLogTaskTimeSheetAdapter(this, backLogTaskTimesheetList)
                            binding!!.recyclerView.adapter = backLogTaskTimeSheetAdapter
                            backLogTaskTimeSheetAdapter!!.notifyDataSetChanged()
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
}