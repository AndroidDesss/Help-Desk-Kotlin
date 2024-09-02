package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.model.BackLogTaskTimeSheetModel

class BackLogTaskTimeSheetAdapter(private val context: Context?, private var backLogTaskTimeSheetModelList: List<BackLogTaskTimeSheetModel>) : RecyclerView.Adapter<BackLogTaskTimeSheetAdapter.ConnectionsHolder>() {
    private var backLogTaskTimeSheetModel: BackLogTaskTimeSheetModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackLogTaskTimeSheetAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_backlog_task_timesheet_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: BackLogTaskTimeSheetAdapter.ConnectionsHolder, position: Int) {
        backLogTaskTimeSheetModel = backLogTaskTimeSheetModelList[position]

        val separatedTaskDate: Array<String> = backLogTaskTimeSheetModel!!.taskDate!!.split("T").toTypedArray()

        holder.screenName.text = backLogTaskTimeSheetModel!!.taskName
        holder.taskDate.text =  separatedTaskDate[0]
        holder.allottedHours.text = backLogTaskTimeSheetModel!!.allottedHours.toString()
        holder.actualHours.text = backLogTaskTimeSheetModel!!.actualWorkedHours.toString()
        holder.taskStatus.text = backLogTaskTimeSheetModel!!.taskStatus
    }
    override fun getItemCount(): Int {
        return backLogTaskTimeSheetModelList.size
    }
    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var screenName: TextView
        var taskDate: TextView
        var allottedHours: TextView
        var actualHours: TextView
        var taskStatus: TextView

        init {
            screenName = itemView.findViewById(R.id.screenNameValue)
            taskDate = itemView.findViewById(R.id.taskDateValue)
            allottedHours = itemView.findViewById(R.id.allottedHoursValue)
            actualHours = itemView.findViewById(R.id.actualHoursValue)
            taskStatus = itemView.findViewById(R.id.taskStatusValue)
        }
    }


}