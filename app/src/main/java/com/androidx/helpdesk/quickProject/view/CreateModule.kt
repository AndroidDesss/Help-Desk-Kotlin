package com.androidx.helpdesk.quickProject.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityCreateModuleBinding
import com.androidx.helpdesk.quickProject.adapter.ModuleAdapter
import com.androidx.helpdesk.quickProject.edit.EditModule
import com.androidx.helpdesk.quickProject.model.ModuleModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CreateModule : AppCompatActivity() {

    private var binding:ActivityCreateModuleBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var moduleId = 0

    private var moduleEstimatedHours = 0

    private var moduleName: String? = null

    private var errMsg: String? = null

    private var projectName: String? = null

    private var moduleStartDate: String? = null

    private var moduleEndDate: String? = null

    private var errorMsg: String? = null

    private var moduleList: MutableList<ModuleModel> = ArrayList()

    private var moduleAdapter: ModuleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_module)
        getBundleData()
        initListener()
    }

    private fun moduleEdit(pid: Int?,mid: Int?) {
        startActivity(Intent(this, EditModule::class.java).putExtra("projectId",pid).putExtra("moduleId",mid))
    }

    private fun moduleDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteModuleById + id,
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
                                errorMsg = loginObject.getString("errorMsg")
                            }
                            if (errorMsg.equals("Deleted Successfully") || errorMsg == "Deleted Successfully") {
                                getModuleList()
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

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.btnNext.setOnClickListener(onClickListener)
        binding!!.addModule.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.addModule ->
            {
                popUpLayout(view)
            }
            R.id.btnNext ->
            {
                startActivity(Intent(this, CreateSubModule::class.java).putExtra("projectId",projectId).putExtra("projectName",projectName))
            }
        }
    }

    private fun getBundleData() {
        val mIntent = intent
        projectId = mIntent.getIntExtra("projectId", 0)
        projectName = mIntent.getStringExtra("projectName")
        getModuleList()
    }

    private fun getModuleList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        moduleList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getModuleList + SharedPref.getCompanyId(this) + "&ProjectID=" + projectId,
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
                                moduleEstimatedHours = loginObject.optInt("EstTotalHours")
                                moduleList.add(ModuleModel(moduleId, moduleName,moduleStartDate,moduleEndDate,moduleEstimatedHours))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            moduleAdapter = ModuleAdapter(this, moduleList)
                            binding!!.recyclerView.adapter = moduleAdapter
                            moduleAdapter!!.notifyDataSetChanged()
                            moduleAdapter!!.setOnClickListener(object :
                                ModuleAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        moduleDelete(model)
                                    }
                                    else
                                    {
                                        moduleEdit(projectId,model)
                                    }
                                }
                            })
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.btnNext.visibility = View.GONE
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

    private fun popUpLayout(view: View) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.add_module_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpSaveBtn=popupView.findViewById(R.id.btnAddModule) as Button
        val popUpProjectName=popupView.findViewById(R.id.projectNameValue) as EditText
        val popUpModuleName=popupView.findViewById(R.id.moduleNameValue) as EditText
        val popUpTotalHours=popupView.findViewById(R.id.totalHoursValue) as EditText
        val popUpStartDate=popupView.findViewById(R.id.startDate) as EditText
        val popUpEndDate=popupView.findViewById(R.id.endDate) as EditText
        val popUpDeliveryDate=popupView.findViewById(R.id.deliveryDateValue) as EditText
        val popUpSequenceNumber=popupView.findViewById(R.id.sequenceNoNameValue) as EditText
        val popUpIsActive=popupView.findViewById(R.id.isActive) as CheckBox

        popUpProjectName.setText(projectName)

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }

        popUpStartDate.setOnClickListener()
        {
            setDate(popUpStartDate)
        }

        popUpEndDate.setOnClickListener()
        {
            setDate(popUpEndDate)
        }

        popUpDeliveryDate.setOnClickListener()
        {
            setDate(popUpDeliveryDate)
        }

        popUpSaveBtn.setOnClickListener()
        {
            if (popUpProjectName.text.toString() == null || popUpProjectName.text.toString().isEmpty()) {
                popUpProjectName.error = "Enter Project Name"
            }
            else if (popUpModuleName.text.toString() == null || popUpModuleName.text.toString().isEmpty())
            {
                popUpModuleName.error = "Enter Module Name"
            }
            else if (popUpTotalHours.text.toString() == null || popUpTotalHours.text.toString().isEmpty()) {
                popUpTotalHours.error = "Enter Total Hours"
            }
            else if (popUpStartDate.text.toString() == null || popUpStartDate.text.toString().isEmpty())
            {
                popUpStartDate.error = "Enter Start Date"
            }
            else if (popUpEndDate.text.toString() == null || popUpEndDate.text.toString().isEmpty()) {
                popUpEndDate.error = "Enter End Date"
            }
            else if (popUpDeliveryDate.text.toString() == null || popUpDeliveryDate.text.toString().isEmpty())
            {
                popUpDeliveryDate.error = "Enter Delivery Date"
            }
            else {
                popupWindow.dismiss()
                addModule(popUpStartDate.text.toString(),popUpEndDate.text.toString(),popUpDeliveryDate.text.toString(),popUpModuleName.text.toString(),popUpTotalHours.text.toString(),popUpSequenceNumber.text.toString(),popUpIsActive.isChecked)
            }
        }
    }

    private fun addModule(startDate: String,endDate: String,deliveryDate: String,moduleName: String,totalHours: String,sequenceNumber: String,isActive : Boolean)
    {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.addModule +startDate+"&EndDate="+ endDate + "&DeliveryDate=" + deliveryDate + "&CompanyID=" +SharedPref.getCompanyId(this) + "&ProjectID=" + projectId+"&ModuleName="+moduleName + "&TotalHrs=" + totalHours + "&Active=" + isActive +"&Sequence=" + sequenceNumber +"&UserName=" +SharedPref.getFirstName(this) ,
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
                            if (errMsg.equals("Module Saved Successfully") || errMsg == "Module Saved Successfully") {
                                CommonMethod.showToast(this, "Added Successfully..!")
                                getModuleList()
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

    override fun onRestart() {
        super.onRestart()
        getModuleList()
    }
}