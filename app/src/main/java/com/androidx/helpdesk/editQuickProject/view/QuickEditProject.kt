package com.androidx.helpdesk.editQuickProject.view

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
import com.androidx.helpdesk.databinding.ActivityCreateQuickEditProjectBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class QuickEditProject : AppCompatActivity() {

    private var binding: ActivityCreateQuickEditProjectBinding? = null

    private var stringRequest: StringRequest? = null

    private var projectId = 0

    private var totalHours = 0

    private var status = 0

    private var projectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_quick_edit_project)
        getBundleData()
        initListener()
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        getProjectDetails()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnSave -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this)) {
                    updateProject()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean {
        if (binding!!.etProjectName.text.toString() == null || binding!!.etProjectName.text.toString().isEmpty()) {
            binding!!.etProjectName.error = "Enter Project Name"
            return false
        }
        else if (binding!!.etTotalHours.text.toString() == null || binding!!.etTotalHours.text.toString().isEmpty())
        {
            binding!!.etTotalHours.error = "Enter Total Hours"
            return false
        }
        else {
            return true
        }
    }

    private fun getProjectDetails()
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.editProjectDetails + projectId,
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
                                projectName = loginObject.getString("ProjectName")
                                totalHours = loginObject.getInt("TotalHrs")
                            }
                            binding!!.etProjectName.setText(projectName)
                            binding!!.etTotalHours.setText(totalHours.toString())
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

    private fun updateProject() {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.updateProjectName + SharedPref.getCompanyId(this) + "&ProjectName=" + binding!!.etProjectName.text.toString() + "&UserName="+ SharedPref.getFirstName(this) + "&ProjectID=" + projectId  + "&TotalHrs=" + binding!!.etTotalHours.text.toString(),
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        startActivity(Intent(this, QuickEditAssignResources::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
                    } else {
                        CommonMethod.showToast(this, "Project Name Exists")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            binding!!.etProjectName.error = "Project Name Exists"
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}