package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.editQuickProject.model.EditAvailableResourceModel


class EditAvailableResourceAdapter(private val context: Context?, private val availableResourceList: List<EditAvailableResourceModel>, private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<EditAvailableResourceAdapter.ConnectionsHolder>() {
    var availableResourceModel: EditAvailableResourceModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAvailableResourceAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_available_resources, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: EditAvailableResourceAdapter.ConnectionsHolder, position: Int) {
        availableResourceModel = availableResourceList[position]
        holder.employeeName.text = availableResourceModel!!.availableEmployeeName
        holder.employeeDepartment.text = availableResourceModel!!.availableDepartment
        holder.employeeDesignation.text = availableResourceModel!!.availableDeisgnation

        holder.employeeId.setOnClickListener{
            OnProgrammerClickListener.onProgrammerItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return availableResourceList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var employeeName: TextView
        var employeeDepartment: TextView
        var employeeDesignation: TextView
        var employeeId: CheckBox
        init {
            employeeName = itemView.findViewById(R.id.emplyoeeName)
            employeeDepartment = itemView.findViewById(R.id.departmentNameValue)
            employeeDesignation = itemView.findViewById(R.id.designationNameValue)
            employeeId = itemView.findViewById(R.id.checkBox)
        }
    }
}