package com.androidx.helpdesk.projects.view

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.androidx.helpdesk.databinding.FragmentProjectBinding
import com.androidx.helpdesk.projects.adapter.ProjectAdapter
import com.androidx.helpdesk.projects.model.ProjectModel
import com.androidx.helpdesk.quickProject.view.CreateQuickProject
import com.androidx.helpdesk.sharedStorage.SharedPref
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale

class ProjectFragment : Fragment() {

    private var binding: FragmentProjectBinding? = null

    private var firstVisit = false

    private var stringRequest: StringRequest? = null

    private var projectAdapter: ProjectAdapter? = null

    private var projectModelList: MutableList<ProjectModel> = ArrayList()

    private var status = 0

    private var projectId = 0

    private var projectType: String? = null

    private var projectName: String? = null

    private var currentTask: String? = null

    private var errorMsg: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project, container, false)
        firstVisit = true
        getProjectList()
        initListener()
        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(projectAdapter!= null)
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
        binding!!.btnCreateQuickProject.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnCreateQuickProject ->{
                val intent = Intent (requireActivity(), CreateQuickProject::class.java)
                requireActivity().startActivity(intent)
            }
        }
    }

    private fun getProjectList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        projectModelList.clear()
        stringRequest = StringRequest(Request.Method.POST, Api.adminProjectList + SharedPref.getUserId(context)+"&CompanyID="+ SharedPref.getCompanyId(context) + "&EmpID=" + SharedPref.getEmployeeId(context),
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
                                projectId = loginObject.getInt("ProjectID")
                                projectName = loginObject.getString("ProjectName")
                                projectType = loginObject.getString("ProjectTypename")
                                currentTask = loginObject.getString("CurrentTask")
                                projectModelList.add(ProjectModel(projectId, projectName,projectType, currentTask))
                            }
                            projectAdapter = ProjectAdapter(context, projectModelList)
                            binding!!.recyclerView.adapter = projectAdapter
                            projectAdapter!!.notifyDataSetChanged()
                            projectAdapter!!.setOnClickListener(object :
                                ProjectAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        CommonMethod.Companion.showAlertDialog(context, "", "Are you sure you want to delete?", "Yes", "No",
                                            object : CommonMethod.DialogClickListener {
                                                override fun dialogOkBtnClicked(value: String?) {
                                                    projectDelete(model)
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

    private fun projectDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(Request.Method.DELETE, Api.deleteProjectById + id,
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
                                errorMsg = loginObject.getString("errorMsg")
                            }
                            if (errorMsg.equals("Deleted Successfully") || errorMsg == "Deleted Successfully") {
                                getProjectList()
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

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
            getProjectList()
        }
    }

    private fun filter(text: String) {
        val filteredlist: ArrayList<ProjectModel> = ArrayList()
        for (item in projectModelList) {
            if ( item.projectName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredlist.add(item)
            }
        }
        if (!filteredlist.isEmpty()) {
            projectAdapter!!.filterList(filteredlist)
        }
    }

}