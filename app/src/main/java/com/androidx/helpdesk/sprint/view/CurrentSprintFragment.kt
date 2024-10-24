package com.androidx.helpdesk.sprint.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.androidx.helpdesk.CommonMethod
import com.androidx.helpdesk.R
import com.androidx.helpdesk.apilist.Api
import com.androidx.helpdesk.backLog.model.BackLogModel
import com.androidx.helpdesk.backLog.view.NewBackLogScreen
import com.androidx.helpdesk.databinding.FragmentCurrentSprintBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import com.androidx.helpdesk.sprint.adapter.CurrentSprintAdapter
import com.androidx.helpdesk.sprint.model.SprintModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale
import java.util.Locale.filter

class CurrentSprintFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentCurrentSprintBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var sprintId = 0

    private var projectId = 0

    private var projectName: String? = null

    private var boardName: String? = null

    private var sprintName: String? = null

    private var sprintStartDate: String? = null

    private var sprintEndDate: String? = null

    private var sprintDeliveryDate: String? = null

    private var sprintEstimatedHours: String? = null

    private var errorMsg: String? = null

    private var currentSprintModelList: MutableList<SprintModel> = ArrayList()

    private var currentSprintAdapter: CurrentSprintAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_sprint, container, false)
        firstVisit = true
        getCurrentSprintList()
        initListener()

        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(currentSprintAdapter!= null)
                {
                    val searchQuery = s.toString().trim()
                    filter(searchQuery)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding!!.getRoot()
    }

    private fun initListener() {
        binding!!.btnNewSprint.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnNewSprint ->{
                val intent = Intent (requireActivity(), NewSprintScreen::class.java)
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        }
        else {
            getCurrentSprintList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getCurrentSprintList()
    {
        CommonMethod.showProgressDialog(context)
        binding!!.rlError.visibility = View.GONE
        currentSprintModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET, Api.getCurrentSprintList +SharedPref.getUserId(context)+"&BoardID"+"&EmpID",
            { ServerResponse ->
                CommonMethod.cancelProgressDialog(context)
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            for (i in 0 until dataArray.length()) {
                                val loginObject = dataArray.getJSONObject(i)
                                sprintId = loginObject.getInt("SprintID")
                                projectId = loginObject.getInt("ProjectID")
                                projectName = loginObject.getString("ProjectName")
                                boardName = loginObject.getString("BoardName")
                                sprintName = loginObject.getString("SprintName")
                                sprintStartDate = loginObject.getString("SprintStartDate")
                                sprintEndDate = loginObject.getString("SprintEndDate")
                                sprintDeliveryDate = loginObject.getString("SprintDeliveryDate")
                                sprintEstimatedHours = loginObject.getString("SprintEstimatedHours")
                                currentSprintModelList.add(SprintModel(sprintId,projectId,projectName,boardName,sprintName,sprintStartDate,sprintEndDate,sprintDeliveryDate,sprintEstimatedHours))
                            }
                            currentSprintAdapter = CurrentSprintAdapter(context, currentSprintModelList)
                            binding!!.recyclerView.adapter = currentSprintAdapter
                            currentSprintAdapter!!.notifyDataSetChanged()
                            currentSprintAdapter!!.setOnClickListener(object :
                                CurrentSprintAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        CommonMethod.showAlertDialog(context, "", "Are you sure you want to delete?", "Yes", "No",
                                            object : CommonMethod.DialogClickListener {
                                                override fun dialogOkBtnClicked(value: String?) {
                                                    deleteSprint(model)
                                                }
                                                override fun dialogNoBtnClicked(value: String?) {}
                                            }
                                        )
                                    }
                                }
                            })
                        } else {
                            binding!!.rlError.visibility = View.VISIBLE
                        }
                    } else {
                        binding!!.rlError.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(context)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(context)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }


    private fun filter(text: String) {
        val filteredlist: ArrayList<SprintModel> = ArrayList()
        for (item in currentSprintModelList) {
            if (item.boardName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredlist.add(item)
            }
        }
        if (!filteredlist.isEmpty()) {
            currentSprintAdapter!!.filterList(filteredlist)
        }
    }

    private fun deleteSprint(id: Int?)
    {
        CommonMethod.showProgressDialog(context)
        stringRequest = StringRequest(
            Request.Method.DELETE,
            Api.deleteSprint + id ,
            { ServerResponse ->
                try {
                    CommonMethod.cancelProgressDialog(context)
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val loginObject = dataArray.getJSONObject(i)
                            errorMsg = loginObject.getString("errorMsg")
                        }
                        if (errorMsg.equals("Deleted Successfully", ignoreCase = true)) {
                            CommonMethod.showToast(context, errorMsg)
                            getCurrentSprintList()
                        }
                    }
                } catch (e: JSONException) {
                    CommonMethod.cancelProgressDialog(context)
                    e.printStackTrace()
                }
            }
        ) {
            CommonMethod.cancelProgressDialog(context)
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
}
