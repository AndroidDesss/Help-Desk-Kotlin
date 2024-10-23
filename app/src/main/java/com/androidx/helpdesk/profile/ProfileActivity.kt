package com.androidx.helpdesk.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.androidx.helpdesk.R
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.databinding.ActivityProfileBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var firstName: String? = null

    private var lastName: String? = null

    private var companyName: String? = null

    private var password: String? = null

    private var address: String? = null

    private var state: String? = null

    private var zipCode: String? = null

    private var email: String? = null

    private var city: String? = null

    private var errorMsg: String? = null

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        initListener()
        clientProfile()
    }

    private fun initListener()
    {
        binding!!.back.setOnClickListener(onClickListener)
        binding!!.btnSave.setOnClickListener(onClickListener)
    }

    private fun clientProfile()
    {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(
            Request.Method.GET,
            Api.getClientProfile + SharedPref.getClientId(this) + "&ClientUserID=" + SharedPref.getEmployeeId(this),
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
                                firstName = loginObject.getString("FirstName")
                                lastName = loginObject.getString("LastName")
                                companyName = loginObject.getString("ClientName")
                                email = loginObject.getString("UserEmail")
                                password = loginObject.getString("ClientPassword")
                                address = loginObject.getString("Address")
                                city = loginObject.getString("City")
                                state = loginObject.getString("State")
                                zipCode = loginObject.getString("Zip")
                            }
                            binding!!.etFirstName.setText(firstName)
                            binding!!.etLastName.setText(lastName)
                            binding!!.etCompanyName.setText(companyName)
                            binding!!.etEmail.setText(email)
                            binding!!.etPassword.setText(password)
                            binding!!.etConfirmPassword.setText(password)
                            binding!!.etAddress.setText(address)
                            binding!!.etCity.setText(city)
                            binding!!.etState.setText(state)
                            binding!!.etZip.setText(zipCode)
                        } else {
                            CommonMethod.Companion.showToast(this, "No data")
                        }
                    } else {
                        CommonMethod.Companion.showToast(this, "No data")
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(this)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            CommonMethod.Companion.showToast(this, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.back -> finish()
            R.id.btnSave -> if (validateDetails()) {
                if (CommonMethod.Companion.isNetworkAvailable(this)) {
                    updateProfile()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
            }
        }
    }

    private fun validateDetails(): Boolean
    {
        if (binding!!.etFirstName.text.toString() == null || binding!!.etFirstName.text.toString().isEmpty())
        {
            binding!!.etFirstName.error = "Enter First Name"
            return false
        }
        else if (binding!!.etLastName.text.toString() == null || binding!!.etLastName.text.toString().isEmpty())
        {
            binding!!.etLastName.error = "Enter Last Name"
            return false
        }
        else if (binding!!.etCompanyName.text.toString() == null || binding!!.etCompanyName.text.toString().isEmpty())
        {
            binding!!.etCompanyName.error = "Enter Company Name"
            return false
        }
        else if (binding!!.etAddress.text.toString() == null || binding!!.etAddress.text.toString().isEmpty())
        {
            binding!!.etAddress.error = "Enter Address"
            return false
        }
        else if (binding!!.etCity.text.toString() == null || binding!!.etCity.text.toString().isEmpty())
        {
            binding!!.etCity.error = "Enter City Name"
            return false
        }
        else if (binding!!.etState.text.toString() == null || binding!!.etState.text.toString().isEmpty())
        {
            binding!!.etState.error = "Enter State Name"
            return false
        }
        else if (binding!!.etZip.text.toString() == null || binding!!.etZip.text.toString().isEmpty())
        {
            binding!!.etZip.error = "Enter Zip Code"
            return false
        }
        else if (binding!!.etPassword.text.toString() == null || binding!!.etPassword.text.toString().isEmpty())
        {
            binding!!.etPassword.error = "Enter Password"
            return false
        }
        else if (binding!!.etConfirmPassword.text.toString() == null || binding!!.etConfirmPassword.text.toString().isEmpty())
        {
            binding!!.etConfirmPassword.error = "Enter Confirm Password"
            return false
        }
        else if (!binding!!.etPassword.text.toString().equals(binding!!.etConfirmPassword.text.toString()))
        {
            binding!!.etConfirmPassword.error = "Password Not Same"
            binding!!.etPassword.error = "Password Not Same"
            return false
        }
        else if (binding!!.etEmail.text.toString() == null || binding!!.etEmail.text.toString().isEmpty())
        {
            binding!!.etEmail.error = "Enter Email Id"
            return false
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding!!.etEmail.text.toString()).matches())
        {
            binding!!.etEmail.error = "Enter Valid Email"
            return false
        }
        else
        {
            return true
        }
    }

    private fun updateProfile() {
        Log.d("ServerResponse", Api.updateProfile + SharedPref.getClientId(this) + "&City=" + binding!!.etCity.text.toString() + "&UserEmail=" + binding!!.etEmail.text.toString() + "&ClientPassword=" + binding!!.etPassword.text.toString() + "&FirstName=" + binding!!.etFirstName.text.toString() + "&LastName=" + binding!!.etLastName.text.toString() + "&ImagePath=" + "" + "&State=" + binding!!.etState.text.toString() + "&Zip=" + binding!!.etZip.text.toString() + "&Address=" + binding!!.etAddress.text.toString() + "&ClientUserID=" + SharedPref.getEmployeeId(this) + "&ClientName=" + binding!!.etCompanyName.text.toString() + "&CompanyID=" + SharedPref.getCompanyId(this))
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.POST, Api.updateProfile + SharedPref.getClientId(this) + "&City=" + binding!!.etCity.text.toString() + "&UserEmail=" + binding!!.etEmail.text.toString() + "&ClientPassword=" + binding!!.etPassword.text.toString() + "&FirstName=" + binding!!.etFirstName.text.toString() + "&LastName=" + binding!!.etLastName.text.toString() + "&ImagePath=" + "~/ChatImages/6_3.jpg" + "&State=" + binding!!.etState.text.toString() + "&Zip=" + binding!!.etZip.text.toString() + "&Address=" + binding!!.etAddress.text.toString() + "&ClientUserID=" + SharedPref.getEmployeeId(this) + "&ClientName=" + binding!!.etCompanyName.text.toString() + "&CompanyID=" + SharedPref.getCompanyId(this),
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(this)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataObject = jsondata.getJSONObject("data")
                        errorMsg = dataObject.getString("Error Msg")
                        CommonMethod.Companion.showToast(this, errorMsg)
                        clientProfile()
                    } else {
                        CommonMethod.Companion.showToast(this, "Credentials Wrong...!")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            CommonMethod.Companion.showToast(this, "Credentials Wrong..")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
}