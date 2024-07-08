package com.androidx.helpdesk.board.view

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
import com.androidx.helpdesk.board.adapter.BoardAdapter
import com.androidx.helpdesk.board.model.BoardModel
import com.androidx.helpdesk.databinding.FragmentBoardBinding
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale


class BoardFragment : Fragment() {

    private var firstVisit = false

    private var binding: FragmentBoardBinding? = null

    private var stringRequest: StringRequest? = null

    private var status = 0

    private var projectId = 0

    private var boardId = 0

    private var projectName: String? = null

    private var boardName: String? = null

    private var boardModelList: MutableList<BoardModel> = ArrayList()

    private var boardAdapter: BoardAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)
        firstVisit = true
        getBoardList()
        initListener()
        binding!!.etProjectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(boardAdapter!= null)
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

    private fun initListener() {
        binding!!.btnBoard.setOnClickListener(onClickListener)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btnBoard ->{
                val intent = Intent (requireActivity(), NewBoardScreen::class.java)
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (firstVisit) {
            firstVisit = false
        } else {
//            getBoardList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getBoardList()
    {
        binding!!.cardView.visibility = View.VISIBLE
        binding!!.rlError.visibility = View.GONE
        boardModelList.clear()
        stringRequest = StringRequest(
            Request.Method.POST, Api.getBoardList ,
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
                                boardId = loginObject.getInt("BoardID")
                                projectId = loginObject.getInt("ProjectID")
                                projectName = loginObject.getString("ProjectName")
                                boardName = loginObject.getString("BoardName")
                                boardModelList.add(BoardModel(boardId,projectId, projectName,boardName))
                            }
                            boardAdapter = BoardAdapter(context, boardModelList)
                            binding!!.recyclerView.adapter = boardAdapter
                            boardAdapter!!.notifyDataSetChanged()
                            boardAdapter!!.setOnClickListener(object :
                                BoardAdapter.OnClickListener {
                                override fun onClick(holder: String, position: Int, model: Int) {
                                    if(holder == "delete")
                                    {
                                        CommonMethod.showAlertDialog(context, "", "Are you sure you want to delete?", "Yes", "No",
                                            object : CommonMethod.DialogClickListener {
                                                override fun dialogOkBtnClicked(value: String?) {
                                                    boardDelete(model)
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

    private fun boardDelete(id: Int?) {
        binding!!.cardView.visibility = View.VISIBLE
        stringRequest = StringRequest(
            Request.Method.POST, Api.boardDelete + id,
            { ServerResponse ->
                binding!!.cardView.visibility = View.GONE
                try {
                    val jsondata = JSONObject(ServerResponse)
                    status = jsondata.getInt("status")
                    if (status == 200) {
                        val dataArray = jsondata.getJSONArray("data")
                        if (dataArray != null && dataArray.length() > 0) {
                            CommonMethod.showToast(context, "Deleted Successfully")
                            getBoardList()
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
        val filteredlist: ArrayList<BoardModel> = ArrayList()
        for (item in boardModelList) {
            if (item.projectName!!.toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))|| item.boardName!!.toLowerCase(
                    Locale.getDefault()).startsWith(
                    text.lowercase(Locale.getDefault())
                )) {
                filteredlist.add(item)
            }
        }
        if (!filteredlist.isEmpty()) {
            boardAdapter!!.filterList(filteredlist)
        }
    }


}