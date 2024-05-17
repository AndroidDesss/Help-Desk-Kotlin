package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.model.EditAssignResourceModel
import com.androidx.helpdesk.editQuickProject.`interface`.OnDeleteListener

class EditAssignedResourceAdapter(private val context: Context?, private val assignResourceList: List<EditAssignResourceModel>,private val OnDeleteClickListener: OnDeleteListener) : RecyclerView.Adapter<EditAssignedResourceAdapter.ConnectionsHolder>() {
    var assignResourceModel: EditAssignResourceModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAssignedResourceAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_assigned_resources, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: EditAssignedResourceAdapter.ConnectionsHolder, position: Int) {
        assignResourceModel = assignResourceList[position]
        holder.resourceName.text = assignResourceModel!!.employeeName

        holder.resourceId.setOnClickListener{
            OnDeleteClickListener.onDeleteItemClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return assignResourceList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var resourceName: TextView
        var resourceId: CheckBox
        init {
            resourceName = itemView.findViewById(R.id.resourceName)
            resourceId = itemView.findViewById(R.id.checkBox)
        }
    }
}