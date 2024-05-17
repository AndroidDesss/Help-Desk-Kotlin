package com.androidx.helpdesk.quickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.quickProject.model.AvailableResourceModel

class AvailableResourceAdapter(private val context: Context?, private val availableResourceList: List<AvailableResourceModel>,private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<AvailableResourceAdapter.ConnectionsHolder>() {
    var availableResourceModel: AvailableResourceModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableResourceAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_available_resources, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: AvailableResourceAdapter.ConnectionsHolder, position: Int) {
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