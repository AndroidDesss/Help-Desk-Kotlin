package com.androidx.helpdesk.quickProject.edit

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityEditModuleBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EditModule : AppCompatActivity() {

    private var binding: ActivityEditModuleBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var moduleId = 0

    private var errMsg: String? = null

    private var moduleName: String? = null

    private var moduleStartDate: String? = null

    private var moduleEndDate: String? = null

    private var moduleEstimatedHours = 0

    private var moduleDeliveryDate: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_module)
        getBundleData()
        initListener()
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        moduleId = mIntent.getIntExtra("moduleId",0)
        getModuleListValues(projectId,moduleId)
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
        binding!!.startDate.setOnClickListener(onClickListener)
        binding!!.endDate.setOnClickListener(onClickListener)
        binding!!.deliveryDateValue.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.startDate ->
            {
                setDate(binding!!.startDate)
            }
            R.id.endDate ->
            {
                setDate(binding!!.endDate)
            }
            R.id.deliveryDateValue ->
            {
                setDate(binding!!.deliveryDateValue)
            }
            R.id.btnSave ->
            {
                if (binding!!.moduleNameValue.text.toString() == null || binding!!.moduleNameValue.text.toString().isEmpty())
                {
                    binding!!.moduleNameValue.error = "Enter Module Name"
                }
                else if (binding!!.startDate.text.toString() == null || binding!!.startDate.text.toString().isEmpty())
                {
                    binding!!.startDate.error = "Enter Start Date"
                }
                else if (binding!!.endDate.text.toString() == null || binding!!.endDate.text.toString().isEmpty()) {
                    binding!!.endDate.error = "Enter End Date"
                }
                else if (binding!!.deliveryDateValue.text.toString() == null || binding!!.deliveryDateValue.text.toString().isEmpty())
                {
                    binding!!.deliveryDateValue.error = "Enter Final Date"
                }
                else if (binding!!.estimatedHoursValue.text.toString() == null || binding!!.estimatedHoursValue.text.toString().isEmpty()) {
                    binding!!.estimatedHoursValue.error = "Enter Estimated Hours"
                }
                else if (binding!!.actualHoursValue.text.toString() == null || binding!!.actualHoursValue.text.toString().isEmpty()) {
                    binding!!.actualHoursValue.error = "Enter Actual Hours"
                }
                else {
                    updateModule(binding!!.moduleNameValue.text.toString(),binding!!.estimatedHoursValue.text.toString(),binding!!.startDate.text.toString(),binding!!.endDate.text.toString(),binding!!.deliveryDateValue.text.toString(),binding!!.actualHoursValue.text.toString())
                }
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
            this,
            { view, year, monthOfYear, dayOfMonth ->
                editText.setText(StringBuilder().append(monthOfYear + 1).append("/").append(dayOfMonth.toString()).append("/").append(year))
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun updateModule(moduleName: String,estTotalHours: String,startDate: String,endDate: String,deliveryDate: String,actTotalHours : String)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.updateModule + projectId +"&ModuleID=" + moduleId + "&CompanyID="+ SharedPref.getCompanyId(this) +"&ModuleName=" + moduleName +"&EstTotalHours=" + estTotalHours +"&EstStartDate=" + startDate +"&EstEndDate=" + endDate + "&FinalEndDate="+ deliveryDate + "&ActTotalHours=" + actTotalHours + "&UserName=" + SharedPref.getFirstName(this),
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                errMsg = loginObject.getString("errorMsg")
                            }
                            if (errMsg.equals("Module Updated Successfully") || errMsg == "Module Updated Successfully") {
                                CommonMethod.showToast(this, "Module Updated Successfully..!")
                                finish()
                            }
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

    private fun getModuleListValues(pId : Int?, mId : Int?)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.editModuleById + SharedPref.getCompanyId(this) + "&ProjectID=" + pId + "&ModuleID=" + mId,
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
                                moduleId = loginObject.getInt("ModuleID")
                                moduleName = loginObject.getString("ModuleName")
                                moduleStartDate = loginObject.optString("EstStartDate")
                                moduleEndDate = loginObject.optString("EstEndDate")
                                moduleEstimatedHours = loginObject.getInt("EstTotalHours")
                                moduleDeliveryDate = loginObject.optString("FinalEndDate")
                            }
                            binding!!.moduleNameValue.setText(moduleName)
                            val separatedStartDate: Array<String> = moduleStartDate!!.split("T").toTypedArray()
                            val separatedEndDate: Array<String> = moduleEndDate!!.split("T").toTypedArray()
                            val separatedDeliveryDate: Array<String> = moduleDeliveryDate!!.split("T").toTypedArray()
                            binding!!.startDate.setText(separatedStartDate[0])
                            binding!!.endDate.setText(separatedEndDate[0])
                            binding!!.deliveryDateValue.setText(separatedDeliveryDate[0])
                            binding!!.estimatedHoursValue.setText(moduleEstimatedHours.toString())
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
}