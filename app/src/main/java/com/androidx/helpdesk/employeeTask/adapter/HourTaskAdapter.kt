package com.androidx.helpdesk.employeeTask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.employeeTask.model.HourTaskModel
import com.androidx.helpdesk.employeeTask.model.NoTaskModel

class HourTaskAdapter(
    private val context: Context?,
    private val hourTaskModelList: List<HourTaskModel>
) : RecyclerView.Adapter<HourTaskAdapter.ConnectionsHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HourTaskAdapter.ConnectionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_less_hour, parent, false)
        return ConnectionsHolder(view)
    }


    override fun onBindViewHolder(holder: HourTaskAdapter.ConnectionsHolder, position: Int) {
        val hourTaskModel = hourTaskModelList[position]

        holder.projectName.text = hourTaskModel.projectName
        holder.moduleName.text = hourTaskModel.moduleName
        holder.taskName.text = hourTaskModel.taskName
        holder.taskStatus.text = hourTaskModel.taskStatus
        holder.allottedHours.text = hourTaskModel.allottedHours.toString()
        holder.allottedDate.text = hourTaskModel.datePart
    }

    override fun getItemCount(): Int {
        return hourTaskModelList.size

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