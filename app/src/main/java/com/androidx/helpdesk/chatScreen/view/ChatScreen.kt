package com.androidx.helpdesk.chatScreen.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.androidx.helpdesk.chatScreen.adapter.ChatAdapter
import com.androidx.helpdesk.chatScreen.model.ChatModel
import com.androidx.helpdesk.databinding.ActivityChatScreenBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject

class ChatScreen : AppCompatActivity() {

    private var binding: ActivityChatScreenBinding? = null

    private var taskMiscId = 0

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var ticketsUrl: String? = null

    private var chatTicketId: String? = null

    private var chatMessage: String? = null

    private var chatClassType: String? = null

    private var chatUserName: String? = null

    private var chatImage: String? = null

    private var chatInitial: String? = null

    private var chatDate: String? = null

    private var errorMsg: String? = null

    private var userName: String? = null

    private var userEmail: String? = null

    var chatList: MutableList<ChatModel> = ArrayList()

    private var chatAdapter: ChatAdapter? = null

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_screen)
        bundleData()
        initListener()
    }

    private fun initListener() {
        binding!!.backButton.setOnClickListener(onClickListener)
        binding!!.actionSend.setOnClickListener(onClickListener)
        binding!!.btnCloseTicket.setOnClickListener(onClickListener)
        binding!!.contentMessage.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding!!.contentMessage.text.toString() == null || binding!!.contentMessage.text.toString().isEmpty() ||binding!!.contentMessage.text.toString().trim().isEmpty()) {
                binding!!.actionSend.visibility=View.GONE
            }
            else
            {
                binding!!.actionSend.visibility=View.VISIBLE
            }
        }
    }

    private fun bundleData()
    {
        if(SharedPref.getScreenId(this)==3)
        {
            binding!!.btnCloseTicket.text="ReOpen Ticket"
            ticketsUrl=Api.reOpenTickets
        }
        else
        {
            binding!!.btnCloseTicket.text="Close Ticket"
            ticketsUrl=Api.closeTickets
        }
        val intent = intent
        taskMiscId = intent.getIntExtra("TaskId", 0)
        getChatList(taskMiscId)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.backButton -> finish()
            R.id.btnCloseTicket ->
                CommonMethod.Companion.showAlertDialog(this, "", "Are you sure?", "Yes", "No",
                    object : CommonMethod.DialogClickListener {
                        override fun dialogOkBtnClicked(value: String?) {
                            closeTicket(view)
                        }
                        override fun dialogNoBtnClicked(value: String?) {}
                    }
                )
            R.id.actionSend ->
                if (CommonMethod.Companion.isNetworkAvailable(this))
                {
                    CommonMethod.showProgressDialog(this)
                    postMessage()
                } else {
                    CommonMethod.Companion.showToast(this, "Check Internet")
                }
        }
    }

    private fun getChatList(tasKId: Int)
    {
        CommonMethod.showProgressDialog(this)
        chatList.clear()
        stringRequest = StringRequest(Request.Method.GET, Api.getChatList + tasKId,
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
                                chatTicketId = loginObject.getString("TicketId")
                                chatMessage = loginObject.getString("Message")
                                chatClassType = loginObject.getString("ClassType")
                                chatUserName = loginObject.getString("UserName1")
                                chatImage = loginObject.getString("ImagePathname")
                                chatInitial = loginObject.getString("initial")
                                chatDate = loginObject.getString("StartDate")
                                chatList.add(ChatModel(chatTicketId, chatMessage,chatClassType,chatUserName, chatImage, chatInitial, chatDate))
                            }
                            chatAdapter = ChatAdapter(this, chatList)
                            binding!!.recyclerView.adapter = chatAdapter
                            chatAdapter!!.notifyDataSetChanged()
                        }
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

    private fun postMessage() {
        stringRequest = StringRequest(Request.Method.POST, Api.postChatMessage + binding!!.contentMessage.text.toString() + "&EmpID=" + SharedPref.getClientId(this)+ "&ClientUserID=" + SharedPref.getEmployeeId(this)+ "&UserName=" + SharedPref.getUserName(this)+ "&TicketId=" + taskMiscId+ "&UserId=" + SharedPref.getClientId(this)+"&client="+"true",
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Record Updated") || errorMsg == "Record Updated") {
                            binding!!.contentMessage.text=null
                            getChatList(taskMiscId)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Credentials Wrong")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun popUpLayout(view: View,etName: String,etEmail: String) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.feedback_pop_up, null)
        val wid = LinearLayout.LayoutParams.MATCH_PARENT
        val high = LinearLayout.LayoutParams.MATCH_PARENT
        val focus= true
        val popupWindow = PopupWindow(popupView, wid, high, focus)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val popUpCloseBtn=popupView.findViewById(R.id.btnClose) as Button
        val popUpRatingBar=popupView.findViewById(R.id.ratingBar) as RatingBar
        val popUpFirstNameEt=popupView.findViewById(R.id.etFirstName) as EditText
        val popUpEmailEt=popupView.findViewById(R.id.etEmail) as EditText
        val popUpCommentsEt=popupView.findViewById(R.id.etComments) as EditText
        val popUpSaveBtn=popupView.findViewById(R.id.btnSave) as Button
        popUpFirstNameEt.setText(etName)
        popUpEmailEt.setText(etEmail)

        popUpCloseBtn.setOnClickListener()
        {
            popupWindow.dismiss()
        }

        popUpSaveBtn.setOnClickListener()
        {
            if (popUpFirstNameEt.text.toString() == null || popUpFirstNameEt.text.toString().isEmpty()) {
                popUpFirstNameEt.error = "Enter First Name"
            }
            else if (popUpEmailEt.text.toString() == null || popUpEmailEt.text.toString().isEmpty()) {
                popUpEmailEt.error = "Enter Email Id"
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(popUpEmailEt.text.toString()).matches())
            {
                popUpEmailEt.error = "Enter Valid Email Id"
            }
            else
            {
                popupWindow.dismiss()
                saveFeedback(popUpRatingBar.rating,popUpFirstNameEt.text.toString(),popUpEmailEt.text.toString(),popUpCommentsEt.text.toString())
            }
        }
    }


    private fun saveFeedback(noOfStars: Float,name: String,email: String,comments: String) {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.POST, Api.postFeedBack + noOfStars + "&TicketId=" + taskMiscId+ "&Name=" + name+ "&email=" + email+ "&Comments=" + comments,
            { ServerResponse ->
                try {
                    CommonMethod.cancelProgressDialog(this)
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Record Updated") || errorMsg == "Record Updated") {
                            finish()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Credentials Wrong")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun closeTicket(view: View) {
        CommonMethod.showProgressDialog(this)
        stringRequest = StringRequest(Request.Method.POST, ticketsUrl + taskMiscId ,
            { ServerResponse ->
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Record Updated") || errorMsg == "Record Updated") {

                            if(SharedPref.getScreenId(this)==3)
                            {
                                finish()
                            }
                            else
                            {
                                getUserNameEmail(view)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(this)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(this, "Credentials Wrong")
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun getUserNameEmail(view: View)
    {
        stringRequest = StringRequest(Request.Method.GET, Api.getUserNameEmail + SharedPref.getEmployeeId(this),
            { ServerResponse ->
                try {
                    CommonMethod.cancelProgressDialog(this)
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                userName = loginObject.getString("Name")
                                userEmail = loginObject.getString("UserEmail")
                            }
                            popUpLayout(view,userName!!,userEmail!!)
                        }
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