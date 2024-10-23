package com.androidx.helpdesk.sprint.view

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
import com.androidx.helpdesk.databinding.FragmentSprintDetailsBinding
import com.androidx.helpdesk.sprint.adapter.CurrentSprintAdapter
import com.androidx.helpdesk.sprint.adapter.SprintDetailsAdapter
import com.androidx.helpdesk.sprint.model.SprintDetailsModel
import com.androidx.helpdesk.sprint.model.SprintModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale


class SprintDetailsFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentSprintDetailsBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var sprintListDetailsId = 0

    private var sprintDetailsId = 0

    private var taskName: String? = null

    private var projectName: String? = null

    private var description: String? = null

    private var empName: String? = null

    private var allottedHours: Double? = null

    private var actualHours: Double? = null

    private var taskDate: String? = null

    private var errorMsg: String? = null

    private var sprintDetailsModelList: MutableList<SprintDetailsModel> = ArrayList()

    private var sprintDetailsAdapter: SprintDetailsAdapter? = null

    companion object {
        private const val ARG_SPRINT_ID = "sprintId"

        fun newInstance(sprintId: Int): SprintDetailsFragment {
            val fragment = SprintDetailsFragment()
            val args = Bundle()
            args.putInt(ARG_SPRINT_ID, sprintId)
            fragment.arguments = args
            return fragment
        }
    }

    private var sprintId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sprintId = it.getInt(ARG_SPRINT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sprint_details, container, false)
        firstVisit = true
        getSprintDetailsList()

        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(sprintDetailsAdapter!= null)
                {
                    val searchQuery = s.toString().trim()
                    filter(searchQuery)
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        }
        else {
            getSprintDetailsList()
        }
    }

    private fun getSprintDetailsList()
    {
        CommonMethod.showProgressDialog(context)
        binding!!.rlError.visibility = View.GONE
        sprintDetailsModelList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.sprintDetailsList + sprintId,
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
                                sprintListDetailsId = loginObject.getInt("SprintDetailID")
                                sprintDetailsId = loginObject.getInt("SprintID")
                                taskName = loginObject.getString("TaskName")
                                projectName = loginObject.getString("ProjectName")
                                description = loginObject.getString("Description")
                                empName = loginObject.getString("EmpName")
                                allottedHours = loginObject.getDouble("AllotedHrs")
                                actualHours = loginObject.getDouble("ActualHours")
                                taskDate = loginObject.getString("TaskDate")
                                sprintDetailsModelList.add(SprintDetailsModel(sprintListDetailsId,sprintDetailsId,taskName,projectName,description,empName,allottedHours,actualHours,taskDate))
                            }
                            sprintDetailsAdapter = SprintDetailsAdapter(context, sprintDetailsModelList)
                            binding!!.recyclerView.adapter = sprintDetailsAdapter
                            sprintDetailsAdapter!!.notifyDataSetChanged()
                            sprintDetailsAdapter!!.setOnClickListener(object :
                                SprintDetailsAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        CommonMethod.showAlertDialog(context, "", "Are you sure you want to delete?", "Yes", "No",
                                            object : CommonMethod.DialogClickListener {
                                                override fun dialogOkBtnClicked(value: String?) {
                                                    deleteSprintDetails(model)
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
        val filteredList: ArrayList<SprintDetailsModel> = ArrayList()
        for (item in sprintDetailsModelList) {
            if (item.taskName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredList.add(item)
            }
        }
        if (!filteredList.isEmpty()) {
            sprintDetailsAdapter!!.filterList(filteredList)
        }
    }

    private fun deleteSprintDetails(id: Int?)
    {
        CommonMethod.showProgressDialog(context)
        stringRequest = StringRequest(
            Request.Method.DELETE,
            Api.deleteSprintDetails + id ,
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
                            getSprintDetailsList()
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