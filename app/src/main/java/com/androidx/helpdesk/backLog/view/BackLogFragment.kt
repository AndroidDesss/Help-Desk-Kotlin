package com.androidx.helpdesk.backLog.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.androidx.helpdesk.databinding.FragmentBackLogBinding
import com.androidx.helpdesk.quickProject.view.CreateQuickProject
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class BackLogFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentBackLogBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectTaskId = 0

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_back_log, container, false)
        firstVisit = true
        getBackLogList()
        initListener()
        return binding!!.getRoot()
    }

    private fun initListener() {
        binding!!.btnNewBackLog.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnNewBackLog ->{
                val intent = Intent (requireActivity(), NewBackLogScreen::class.java)
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
            getBackLogList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBackLogList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        backLogModelList.clear()
        stringRequest = StringRequest(
            Request.Method.GET, Api.getBackLogList +  SharedPref.getCompanyId(context) ,
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
                                backLogModelList.add(BackLogModel(projectTaskId,taskName, projectName,moduleName, startDate,cardViewStatus,priority))
                            }
                            backLogAdapter = BackLogAdapter(context, backLogModelList)
                            binding!!.recyclerView.adapter = backLogAdapter
                            backLogAdapter!!.notifyDataSetChanged()
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
}