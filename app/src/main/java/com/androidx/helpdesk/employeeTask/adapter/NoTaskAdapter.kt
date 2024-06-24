package com.androidx.helpdesk.employeeTask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.employeeTask.model.NoTaskModel

class NoTaskAdapter(
    private val context: Context?,
    private val noTaskModelList: List<NoTaskModel>
) : RecyclerView.Adapter<NoTaskAdapter.ConnectionsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_no_task, parent, false)
        return ConnectionsHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        val noTaskModel = noTaskModelList[position]
        holder.empName.text = noTaskModel.empName
    }

    override fun getItemCount(): Int {
        return noTaskModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val empName: TextView = itemView.findViewById(R.id.empName)
    }
}
