package com.androidx.helpdesk.authentication

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
import com.androidx.helpdesk.databinding.ActivityLoginBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var employeeId = 0

    private var clientId = 0

    private var departmentId = 0

    private var userTypeId: String? = null

    private var userType: String? = null

    private var companyId: String? = null

    private var firstName: String? = null

    private var lastName: String? = null

    private var userName = ""

    private var clientName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initListener()
    }
    private fun initListener() {
        binding!!.btnLogin.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnLogin -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this)) {
                    validateUser()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean {
        if (binding!!.etCompany.text.toString() == null || binding!!.etCompany.text.toString().isEmpty()) {
            binding!!.etCompany.error = "Enter Company Name"
            return false
        }
        else if (binding!!.etEmail.text.toString() == null || binding!!.etEmail.text.toString().isEmpty())
        {
            binding!!.etEmail.error = "Enter Email Id"
            return false
        }
        else if (binding!!.etPassword.text.toString() == null || binding!!.etPassword.text.toString().isEmpty()) {
            binding!!.etPassword.error = "Enter Password"
            return false
        }
        else {
            return true
        }
    }

    private fun validateUser() {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.POST, Api.login + binding!!.etCompany.text.toString() + "&EmailID=" + binding!!.etEmail.text.toString() + "&Password=" + binding!!.etPassword.text.toString(),
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            userType = loginObject.getString("UserType")
                            if (userType.equals("Client") || userType == "Client") {
                                employeeId = loginObject.getInt("ClientUserID")
                                clientId = loginObject.getInt("ClientID")
                                userName = loginObject.getString("UserName")
                                clientName = loginObject.getString("ClientName")
                                firstName = loginObject.getString("FirstName")
                                lastName = loginObject.getString("LastName")
                                companyId = loginObject.getString("CompanyID")
                                userTypeId = loginObject.getString("UserTypeID")
                            } else if (userType.equals("Admin") || userType == "Admin") {
                                companyId = loginObject.getString("CompanyID")
                                employeeId = loginObject.getInt("EmpID")
                                userTypeId = loginObject.getString("UserTypeID")
                                firstName = loginObject.getString("FirstName")
                                lastName = loginObject.getString("LastName")
                                departmentId = loginObject.getInt("DeptID")
                            } else if (userType.equals("User") || userType == "User") {
                                userTypeId = loginObject.getString("UserTypeID")
                                companyId = loginObject.getString("CompanyID")
                                employeeId = loginObject.getInt("EmpID")
                                firstName = loginObject.getString("FirstName")
                                lastName = loginObject.getString("LastName")
                                departmentId = loginObject.getInt("DeptID")
                            }
                        }
                        SharedPref.setClientId(this, clientId)
                        SharedPref.setClientName(this, clientName)
                        SharedPref.setCompanyId(this, companyId)
                        SharedPref.setEmployeeId(this, employeeId)
                        SharedPref.setUserId(this, userTypeId)
                        SharedPref.setUserType(this, userType)
                        SharedPref.setFirstName(this, firstName)
                        SharedPref.setLastName(this, lastName)
                        SharedPref.setUserName(this, userName)
                        SharedPref.setDepartmentId(this, departmentId)
                        SharedPref.setUserLoggedIn(this, true)
                        startActivity(Intent(this, DashBoardActivity::class.java))
                        finish()
                    } else {
                        CommonMethod.showToast(this, "Credentials Wrong...!")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Credentials Wrong")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onBackPressed() {
        CommonMethod.Companion.showAlertDialog(this, "", "Are you sure you want to exit?", "Yes", "No",
            object : CommonMethod.DialogClickListener {
                override fun dialogOkBtnClicked(value: String?) {
                    finishAffinity()
                }
                override fun dialogNoBtnClicked(value: String?) {}
            }
        )
    }
}