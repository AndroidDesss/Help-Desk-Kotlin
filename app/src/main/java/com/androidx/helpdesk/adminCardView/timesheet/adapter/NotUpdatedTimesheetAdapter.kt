package com.androidx.helpdesk.adminCardView.timesheet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheet.model.NotUpdatedTimeSheetModel

class NotUpdatedTimesheetAdapter(
    private val context: Context?,
    private val notUpdatedTimeSheetModelList: List<NotUpdatedTimeSheetModel>
) : RecyclerView.Adapter<NotUpdatedTimesheetAdapter.ConnectionsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_no_task, parent, false)
        return ConnectionsHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        val noTaskModel = notUpdatedTimeSheetModelList[position]
        holder.empName.text = noTaskModel.empName
    }

    override fun getItemCount(): Int {
        return notUpdatedTimeSheetModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val empName: TextView = itemView.findViewById(R.id.empName)
    }
}