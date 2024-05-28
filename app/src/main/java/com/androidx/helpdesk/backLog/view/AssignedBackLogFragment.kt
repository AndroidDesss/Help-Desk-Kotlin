package com.androidx.helpdesk.backLog.view

import android.annotation.SuppressLint
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
import com.androidx.helpdesk.backLog.adapter.BackLogAdapter
import com.androidx.helpdesk.backLog.model.BackLogModel
import com.androidx.helpdesk.databinding.FragmentAssignedBackLogBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale


class AssignedBackLogFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentAssignedBackLogBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectTaskId = 0

    private var projectId = 0

    private var moduleId = 0

    private var taskCategoryId = 0

    private var taskName: String? = null

    private var projectName: String? = null

    private var moduleName: String? = null

    private var startDate: String? = null

    private var cardViewStatus: String? = null

    private var priority: String? = null

    private var backLogModelList: MutableList<BackLogModel> = ArrayList()

    private var backLogAdapter: BackLogAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assigned_back_log, container, false)
        firstVisit = true
        getAssignedBackLogList()
        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(backLogAdapter!= null)
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
            getAssignedBackLogList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAssignedBackLogList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        backLogModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET, Api.assignedBackLogList +  SharedPref.getCompanyId(context) ,
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
                                projectTaskId = loginObject.getInt("PrjTaskID")
                                taskName = loginObject.getString("TaskName")
                                projectName = loginObject.getString("ProjectName")
                                moduleName = loginObject.getString("ModuleName")
                                startDate = loginObject.getString("EstStartDate")
                                cardViewStatus = loginObject.getString("CardviewStatus")
                                priority = loginObject.getString("Priority")
                                projectId = loginObject.getInt("ProjectID")
                                moduleId = loginObject.getInt("ModuleID")
                                taskCategoryId = loginObject.getInt("TaskCategory")
                                backLogModelList.add(BackLogModel(projectTaskId,taskName, projectName,moduleName, startDate,cardViewStatus,priority,projectId,moduleId,taskCategoryId))
                            }
                            backLogAdapter = BackLogAdapter(context, backLogModelList)
                            binding!!.recyclerView.adapter = backLogAdapter
                            backLogAdapter!!.notifyDataSetChanged()
                            backLogAdapter!!.setOnClickListener(object :
                                BackLogAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        CommonMethod.Companion.showAlertDialog(context, "", "Are you sure you want to delete?", "Yes", "No",
                                            object : CommonMethod.DialogClickListener {
                                                override fun dialogOkBtnClicked(value: String?) {
                                                    assignedBackLogDelete(model)
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
                    binding!!.cardView.visibility = View.GONE
                    e.printStackTrace()
                }
            }
        ) {
            binding!!.cardView.visibility = View.GONE
            stringRequest!!.retryPolicy = DefaultRetryPolicy(100, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun assignedBackLogDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.deleteBackLog + id,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            CommonMethod.showToast(context, "Deleted Successfully")
                            getAssignedBackLogList()
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
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<BackLogModel> = ArrayList()
        for (item in backLogModelList) {
            if (item.taskName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredlist.add(item)
            }
        }
        if (!filteredlist.isEmpty()) {
            backLogAdapter!!.filterList(filteredlist)
        }
    }


}