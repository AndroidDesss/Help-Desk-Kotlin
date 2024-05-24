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
import com.androidx.helpdesk.databinding.FragmentPendingSprintBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import com.androidx.helpdesk.sprint.adapter.PendingSprintAdapter
import com.androidx.helpdesk.sprint.model.SprintModel
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale

class PendingSprintFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentPendingSprintBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var sprintId = 0

    private var projectName: String? = null

    private var boardName: String? = null

    private var sprintName: String? = null

    private var sprintStartDate: String? = null

    private var sprintEndDate: String? = null

    private var sprintDeliveryDate: String? = null

    private var sprintEstimatedHours: String? = null

    private var pendingSprintModelList: MutableList<SprintModel> = ArrayList()

    private var pendingSprintAdapter: PendingSprintAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_sprint, container, false)
        firstVisit = true
        getPendingSprintList()

        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(pendingSprintAdapter!= null)
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
            getPendingSprintList()
        }
    }

    private fun getPendingSprintList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        pendingSprintModelList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getPendingSprintList + SharedPref.getUserId(context)+"&BoardID"+"&EmpID",
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
                                sprintId = loginObject.getInt("SprintID")
                                projectName = loginObject.getString("ProjectName")
                                boardName = loginObject.getString("BoardName")
                                sprintName = loginObject.getString("SprintName")
                                sprintStartDate = loginObject.getString("SprintStartDate")
                                sprintEndDate = loginObject.getString("SprintEndDate")
                                sprintDeliveryDate = loginObject.getString("SprintDeliveryDate")
                                sprintEstimatedHours = loginObject.getString("SprintEstimatedHours")
                                pendingSprintModelList.add(SprintModel(sprintId,projectName,boardName,sprintName,sprintStartDate,sprintEndDate,sprintDeliveryDate,sprintEstimatedHours))
                            }
                            pendingSprintAdapter = PendingSprintAdapter(context, pendingSprintModelList)
                            binding!!.recyclerView.adapter = pendingSprintAdapter
                            pendingSprintAdapter!!.notifyDataSetChanged()
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

    private fun filter(text: String) {
        val filteredlist: ArrayList<SprintModel> = ArrayList()
        for (item in pendingSprintModelList) {
            if (item.boardName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredlist.add(item)
            }
        }
        if (!filteredlist.isEmpty()) {
            pendingSprintAdapter!!.filterList(filteredlist)
        }
    }
}