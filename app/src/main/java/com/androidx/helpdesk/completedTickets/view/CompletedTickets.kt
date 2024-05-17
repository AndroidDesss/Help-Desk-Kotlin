package com.androidx.helpdesk.completedTickets.view

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
import com.androidx.helpdesk.completedTickets.adapter.CompletedTicketAdapter
import com.androidx.helpdesk.completedTickets.model.CompletedTicketModel
import com.androidx.helpdesk.databinding.FragmentCompletedTicketsBinding
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class CompletedTickets : Fragment() {

    private var binding:FragmentCompletedTicketsBinding? = null

    private var firstVisit = false

    private var status = 0

    private var miscId = 0

    private var ticketSeverity = 0

    private var ticketName: String? = null

    private var ticketSubject: String? = null

    private var ticketCreatedOn: String? = null

    private var stringRequest: StringRequest? = null

    private var completedTicketAdapter: CompletedTicketAdapter? = null

    var completedTicketModelList: MutableList<CompletedTicketModel> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_completed_tickets, container, false)
        firstVisit = true
        completedTicketList()
        return binding!!.getRoot()
    }

    private fun completedTicketList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        completedTicketModelList.clear()
        stringRequest = StringRequest(Request.Method.GET, Api.completedTickets + SharedPref.getUserId(context) + "&EmpID=" + SharedPref.getEmployeeId(context),
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
                                miscId = loginObject.getInt("MiscEID")
                                ticketName = loginObject.getString("TicketNumber")
                                ticketSubject = loginObject.getString("Subject")
                                ticketCreatedOn = loginObject.getString("CreatedOn")
                                ticketSeverity = loginObject.getInt("TicketSeverity")
                                completedTicketModelList.add(CompletedTicketModel(miscId, ticketName,ticketCreatedOn, ticketSubject, ticketSeverity))
                            }
                            completedTicketAdapter = CompletedTicketAdapter(context, completedTicketModelList)
                            binding!!.recyclerView.adapter = completedTicketAdapter
                            completedTicketAdapter!!.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            completedTicketList()
        }
    }
}