package com.androidx.helpdesk.sprint.view

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
import com.androidx.helpdesk.databinding.FragmentPendingTaskBinding
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.sprint.adapter.PendingTaskAdapter
import com.androidx.helpdesk.sprint.model.PendingTaskModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale

class PendingTaskFragment : Fragment(), OnProgrammerListener {

    private var firstVisit = false

    private var binding: FragmentPendingTaskBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId: Int = 0

    private var sequence = 0

    private var sprintId = 0

    private var prjTaskAllotId = 0

    private var taskName: String? = null

    private var errMsg: String? = null

    private var projectName: String? = null

    private var description: String? = null

    private var empName: String? = null

    private var allotedHours: Double? = null

    private var taskDate: String? = null

    private var pendingTaskModelList: MutableList<PendingTaskModel> = ArrayList()

    private var pendingTaskAdapter: PendingTaskAdapter? = null

    private var taskIdList = ArrayList<Int>()

    companion object {
        private const val ARG_PROJECT_ID = "projectId"
        private const val ARG_SPRINT_ID = "sprintId"
        fun newInstance(projectId: Int,sprintId: Int): PendingTaskFragment {
            val fragment = PendingTaskFragment()
            val args = Bundle()
            args.putInt(ARG_PROJECT_ID, projectId)
            args.putInt(ARG_SPRINT_ID, sprintId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectId = it.getInt(ARG_PROJECT_ID)
            sprintId = it.getInt(ARG_SPRINT_ID)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_task, container, false)
        firstVisit = true
        initListener()
        getPendingTaskList()

        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(pendingTaskAdapter!= null)
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
            getPendingTaskList()
        }
    }

    private fun initListener() {
        binding!!.btnNext.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnNext ->
            {
                if(taskIdList.isEmpty() || taskIdList==null )
                {
                    CommonMethod.showToast(context, "Please Select the Task..!")
                }
                else
                {
                    addPendingTask(taskIdList,sprintId)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPendingTaskList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        pendingTaskModelList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getPendingTaskSprintList + projectId,
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
                                taskName = loginObject.getString("TaskName")
                                projectName = loginObject.getString("ProjectName")
                                description = loginObject.getString("Description")
                                empName = loginObject.getString("EmpName")
                                allotedHours = loginObject.getDouble("AllotedHrs")
                                sequence = loginObject.getInt("Sequence")
                                taskDate = loginObject.getString("TaskDate")
                                prjTaskAllotId = loginObject.getInt("PrjTaskAllotID")
                                pendingTaskModelList.add(PendingTaskModel(taskName,projectName,description,empName,allotedHours,sequence,taskDate,prjTaskAllotId))
                            }
                            binding!!.btnNext.visibility = View.VISIBLE
                            pendingTaskAdapter = PendingTaskAdapter(context, pendingTaskModelList,this)
                            binding!!.recyclerView.adapter = pendingTaskAdapter
                            pendingTaskAdapter!!.notifyDataSetChanged()
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
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    override fun onProgrammerItemClicked(position: Int) {
        if (taskIdList.contains(pendingTaskModelList[position].prjTaskAllotId))
        {
            taskIdList.remove(pendingTaskModelList[position].prjTaskAllotId)
        }
        else
        {
            taskIdList.add(pendingTaskModelList[position].prjTaskAllotId)
        }
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<PendingTaskModel> = ArrayList()
        for (item in pendingTaskModelList) {
            if (item.taskName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredList.add(item)
            }
        }
        if (!filteredList.isEmpty()) {
            pendingTaskAdapter!!.filterList(filteredList)
        }
    }

    private fun addPendingTask(taskIds: ArrayList<Int>,sprintId: Int)
    {
        val tIds = taskIds.joinToString(",")
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.addPendingTask + tIds + "&SprintID=" + sprintId,
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
                                errMsg = loginObject.getString("errorMsg")
                            }
                            if (errMsg.equals("Sprint Added Successfully") || errMsg == "Sprint Added Successfully") {
                                taskIdList.clear()
                                CommonMethod.showToast(context, "Sprint Added Successfully..!")
                                getPendingTaskList()
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
            CommonMethod.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

}