package com.androidx.helpdesk.editQuickProject.view

import android.annotation.SuppressLint
import android.content.Intent
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
import com.androidx.helpdesk.databinding.ActivityBackLogTaskAllotedBinding
import com.androidx.helpdesk.databinding.ActivityQuickEditSubModuleBinding
import com.androidx.helpdesk.editQuickProject.adapter.BackLogTaskAllottedAdapter
import com.androidx.helpdesk.editQuickProject.model.BackLogTaskAllottedModel
import com.androidx.helpdesk.projects.adapter.ProjectAdapter
import com.androidx.helpdesk.projects.model.ProjectModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class BackLogTaskAlloted : AppCompatActivity() {

    private var binding: ActivityBackLogTaskAllotedBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectTaskId = 0

    private var prjTaskId = 0

    private var sequenceNo = 0

    private var taskDate: String? = null

    private var employeeName: String? = null

    private var taskCategory: String? = null

    private var actualHours:Double = 0.0

    private var allottedHours:Double = 0.0

    private var backLogTaskAllottedAdapter: BackLogTaskAllottedAdapter? = null

    private var backLogTaskAllottedList: MutableList<BackLogTaskAllottedModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_back_log_task_alloted)
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
        getBackLogTaskAllottedList(projectTaskId)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBackLogTaskAllottedList(projectTaskId: Int)
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        backLogTaskAllottedList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.backLogTaskAllottedList + projectTaskId,
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
                                prjTaskId = loginObject.optInt("PrjTaskAllotID")
                                taskDate = loginObject.optString("TaskDate")
                                sequenceNo = loginObject.optInt("Sequence")
                                employeeName = loginObject.optString("EmpName")
                                taskCategory = loginObject.optString("Description")
                                actualHours = loginObject.optDouble("ActualHours")
                                allottedHours = loginObject.optDouble("AllotedHrs")
                                backLogTaskAllottedList.add(BackLogTaskAllottedModel(prjTaskId, taskDate,sequenceNo, employeeName,taskCategory,actualHours,allottedHours))
                            }
                            backLogTaskAllottedAdapter = BackLogTaskAllottedAdapter(this, backLogTaskAllottedList)
                            binding!!.recyclerView.adapter = backLogTaskAllottedAdapter
                            backLogTaskAllottedAdapter!!.notifyDataSetChanged()
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
}