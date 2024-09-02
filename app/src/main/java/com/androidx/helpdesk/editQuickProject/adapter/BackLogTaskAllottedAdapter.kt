package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.model.BackLogTaskAllottedModel
import com.androidx.helpdesk.editQuickProject.view.BackLogTaskTimesheet

class BackLogTaskAllottedAdapter(private val context: Context?, private var backLogTaskAllottedList: List<BackLogTaskAllottedModel>) : RecyclerView.Adapter<BackLogTaskAllottedAdapter.ConnectionsHolder>() {
    private var backLogTaskAllottedModel: BackLogTaskAllottedModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackLogTaskAllottedAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_backlog_taskallotted_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: BackLogTaskAllottedAdapter.ConnectionsHolder, position: Int) {
        backLogTaskAllottedModel = backLogTaskAllottedList[position]

        val separatedTaskDate: Array<String> = backLogTaskAllottedModel!!.taskDate!!.split("T").toTypedArray()

        holder.employeeName.text = backLogTaskAllottedModel!!.employeeName
        holder.taskDate.text =  separatedTaskDate[0]
        holder.sequenceNo.text = backLogTaskAllottedModel!!.sequenceNo.toString()
        holder.taskCategory.text = backLogTaskAllottedModel!!.taskCategory
        holder.allottedHours.text = backLogTaskAllottedModel!!.allottedHours.toString()
        holder.actualHours.text = backLogTaskAllottedModel!!.actualHours.toString()
    }
    override fun getItemCount(): Int {
        return backLogTaskAllottedList.size
    }
    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var employeeName: TextView
        var taskDate: TextView
        var sequenceNo: TextView
        var taskCategory: TextView
        var allottedHours: TextView
        var actualHours: TextView

        init {
            employeeName = itemView.findViewById(R.id.employeeName)
            taskDate = itemView.findViewById(R.id.taskDateValue)
            sequenceNo = itemView.findViewById(R.id.sequenceNoValue)
            taskCategory = itemView.findViewById(R.id.taskCategoryValue)
            allottedHours = itemView.findViewById(R.id.allottedHoursValue)
            actualHours = itemView.findViewById(R.id.actualHoursValue)
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            if (v === itemView) {
                val backLogTaskAllottedModel = backLogTaskAllottedList[position]
                val mIntent = Intent(context, BackLogTaskTimesheet::class.java)
                mIntent.putExtra("projectTaskId", backLogTaskAllottedModel.prjTaskId)
                context!!.startActivity(mIntent)
            }
        }
    }


}