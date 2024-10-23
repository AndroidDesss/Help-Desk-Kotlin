package com.androidx.helpdesk.quickProject.view

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
import com.androidx.helpdesk.databinding.ActivityCreateQuickProjectBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class CreateQuickProject : AppCompatActivity() {

    private var binding: ActivityCreateQuickProjectBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var projectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_quick_project)
        initListener()
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
                    createProject()
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

    private fun createProject() {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.POST, Api.createQuickProject + SharedPref.getCompanyId(this) + "&ProjectName=" + binding!!.etProjectName.text.toString() + "&TotalHrs=" + binding!!.etTotalHours.text.toString()+"&CreatedBy="+SharedPref.getFirstName(this),
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            projectId = loginObject.getInt("ProjectID")
                            projectName = loginObject.getString("ProjectName")
                        }
                        startActivity(Intent(this, CreateAssignResources::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
                    } else {
                        CommonMethod.showToast(this, "Project Name Exists")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            binding!!.etProjectName.error = "Project Name Exists"
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}