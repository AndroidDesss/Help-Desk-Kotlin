package com.androidx.helpdesk.adminTimeSheetTask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminTimeSheetTask.model.HaveTaskModel

class HaveTaskAdapter (
    private val context: Context?,
    private val haveTaskModelList: List<HaveTaskModel>
) : RecyclerView.Adapter<HaveTaskAdapter.ConnectionsHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HaveTaskAdapter.ConnectionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_have_task, parent, false)
        return ConnectionsHolder(view)
    }


    override fun onBindViewHolder(holder: HaveTaskAdapter.ConnectionsHolder, position: Int) {
        val hourTaskModel = haveTaskModelList[position]

        holder.projectName.text = hourTaskModel.projectName
        holder.moduleName.text = hourTaskModel.moduleName
        holder.taskName.text = hourTaskModel.taskName
        holder.taskStatus.text = hourTaskModel.taskStatus
        holder.allottedHours.text = hourTaskModel.allottedHours.toString()
        holder.allottedDate.text = hourTaskModel.datePart
    }

    override fun getItemCount(): Int {
        return haveTaskModelList.size

    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName: TextView = itemView.findViewById(R.id.appName)
        val moduleName: TextView = itemView.findViewById(R.id.workStatus)
        val taskName: TextView = itemView.findViewById(R.id.taskNameValue)
        val taskStatus: TextView = itemView.findViewById(R.id.taskStatusValue)
        val allottedDate: TextView = itemView.findViewById(R.id.allotedDateValue)
        val allottedHours: TextView = itemView.findViewById(R.id.allotedHourValue)
    }
}