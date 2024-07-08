package com.androidx.helpdesk.adminCardView.timesheet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheet.model.UpdatedTimeSheetModel

class UpdatedTimeSheetAdapter(private val context: Context?, private val updatedTimeSheetModelList: List<UpdatedTimeSheetModel>) : RecyclerView.Adapter<UpdatedTimeSheetAdapter.ConnectionsHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatedTimeSheetAdapter.ConnectionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_less_hour, parent, false)
        return ConnectionsHolder(view)
    }

    override fun onBindViewHolder(holder: UpdatedTimeSheetAdapter.ConnectionsHolder, position: Int) {
        val updatedTimeSheetModel = updatedTimeSheetModelList[position]

        holder.projectName.text = updatedTimeSheetModel.projectName
        holder.moduleName.text = updatedTimeSheetModel.moduleName
        holder.taskName.text = updatedTimeSheetModel.taskName
        holder.allottedHours.text = updatedTimeSheetModel.allottedHours.toString()
        holder.workingHours.text = updatedTimeSheetModel.workingHours.toString()
        holder.taskStatus.text = updatedTimeSheetModel.taskStatus
        holder.notes.text = updatedTimeSheetModel.notes
    }

    override fun getItemCount(): Int {
        return updatedTimeSheetModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectName: TextView = itemView.findViewById(R.id.projectName)
        val moduleName: TextView = itemView.findViewById(R.id.moduleName)
        val taskName: TextView = itemView.findViewById(R.id.taskNameValue)
        val taskStatus: TextView = itemView.findViewById(R.id.taskStatusValue)
        val allottedHours: TextView = itemView.findViewById(R.id.allotedHourValue)
        val workingHours: TextView = itemView.findViewById(R.id.workingHoursValue)
        val notes: TextView = itemView.findViewById(R.id.notesValue)
    }
}