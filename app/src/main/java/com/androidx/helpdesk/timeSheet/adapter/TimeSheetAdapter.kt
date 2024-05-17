package com.androidx.helpdesk.timeSheet.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.timeSheet.model.TimeSheetModel
import com.androidx.helpdesk.timeSheet.view.TimeSheetDetails

class TimeSheetAdapter(private val context: Context?, private val timeSheetModelList: List<TimeSheetModel>) : RecyclerView.Adapter<TimeSheetAdapter.ConnectionsHolder>() {
    var timeSheetModel: TimeSheetModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_timesheetlist, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        timeSheetModel = timeSheetModelList[position]
        val separatedAssignedDate: Array<String> = timeSheetModel!!.timeSheetAssignedDate!!.split("T").toTypedArray()
        val correctDateFormat: Array<String> = separatedAssignedDate[0].split("-").toTypedArray()

        holder.projectName.text = timeSheetModel!!.timeSheetProjectName
        holder.taskNameValue.text = timeSheetModel!!.timeSheetTaskName
        holder.taskDate.text = correctDateFormat[1]+"/"+correctDateFormat[2]+"/"+correctDateFormat[0]+"\n"+" ( "+timeSheetModel!!.timeSheetAllottedHours.toString()+" Hours )"
        holder.taskDescription.text = timeSheetModel!!.taskDescription
        holder.allottedToValue.text = timeSheetModel!!.timeSheetAllottedTo.toString()
    }

    override fun getItemCount(): Int {
        return timeSheetModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var projectName: TextView
        var taskNameValue: TextView
        var allottedToValue: TextView
        var taskDate: TextView
        var taskDescription: TextView

        init {
            projectName = itemView.findViewById(R.id.projectName)
            taskNameValue = itemView.findViewById(R.id.taskNameValue)
            allottedToValue = itemView.findViewById(R.id.allottedToValue)
            taskDate = itemView.findViewById(R.id.assignedDateValue)
            taskDescription = itemView.findViewById(R.id.taskDetailsValue)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === itemView) {
                val timeSheetModel = timeSheetModelList[adapterPosition]
                val mIntent = Intent(context, TimeSheetDetails::class.java)
                mIntent.putExtra("TaskId", timeSheetModel!!.timeSheetProjectId)
                mIntent.putExtra("TaskName", timeSheetModel!!.timeSheetTaskName)
                context!!.startActivity(mIntent)
            }
        }
    }
}