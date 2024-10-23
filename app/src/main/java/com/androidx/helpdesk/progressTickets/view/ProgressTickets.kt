package com.androidx.helpdesk.progressTickets.view

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
import com.androidx.helpdesk.databinding.FragmentProgressTicketsBinding
import com.androidx.helpdesk.progressTickets.adapter.ProgressTicketAdapter
import com.androidx.helpdesk.progressTickets.model.ProgressTicketModel
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class ProgressTickets : Fragment() {

    private var binding: FragmentProgressTicketsBinding? = null

    private var status = 0

    private var miscId = 0

    private var ticketSeverity = 0

    private var stringRequest: StringRequest? = null

    private var ticketName: String? = null

    private var ticketSubject: String? = null

    private var ticketCreatedOn: String? = null

    private var progressTicketAdapter: ProgressTicketAdapter? = null

    private var progressTicketModelList: MutableList<ProgressTicketModel> = ArrayList()

    private var firstVisit = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_progress_tickets, container, false)
        firstVisit = true
        progressTicketList()
        return binding!!.getRoot()
    }

    private fun progressTicketList()
    {
        CommonMethod.showProgressDialog(context)
        binding!!.rlError.visibility = View.GONE
        progressTicketModelList.clear()
        stringRequest = StringRequest(Request.Method.GET, Api.progressTickets + SharedPref.getUserId(context) + "&EmpID=" + SharedPref.getEmployeeId(context) + "&CompanyID=" + SharedPref.getCompanyId(context),
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
                                miscId = loginObject.getInt("MiscEID")
                                ticketName = loginObject.getString("TicketNumber")
                                ticketSubject = loginObject.getString("Subject")
                                ticketCreatedOn = loginObject.getString("CreatedOn")
                                ticketSeverity = loginObject.getInt("TicketSeverity")

                                progressTicketModelList.add(ProgressTicketModel(miscId,ticketName,ticketCreatedOn,ticketSubject,ticketSeverity))
                            }
                            progressTicketAdapter = ProgressTicketAdapter(context, progressTicketModelList)
                            binding!!.recyclerView.adapter = progressTicketAdapter
                            progressTicketAdapter!!.notifyDataSetChanged()
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
            CommonMethod.Companion.showToast(context, "Please Check your Internet")
        }
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            progressTicketList()
        }
    }



}