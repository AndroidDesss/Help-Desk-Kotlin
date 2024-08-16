package com.androidx.helpdesk.adminCardView.assigned.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.assigned.model.AssignedModel

class AssignedAdapter(private val context: Context?, private var assignedSummaryModelList: List<AssignedModel>) : RecyclerView.Adapter<AssignedAdapter.ConnectionsHolder>() {
    var assignedSummaryModel: AssignedModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_assigned_summary, parent, false)
        return ConnectionsHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        assignedSummaryModel = assignedSummaryModelList[position]

        holder.projectName.text = assignedSummaryModel!!.projectName + "-" + assignedSummaryModel!!.moduleName
        holder.taskNameValue.text = assignedSummaryModel!!.taskName
        holder.allottedByValue.text = assignedSummaryModel!!.createdBy
        holder.createdDateValue.text = assignedSummaryModel!!.createdDate
        holder.estimateCompleteDateValue.text = assignedSummaryModel!!.estCompletedDate
    }

    override fun getItemCount(): Int {
        return assignedSummaryModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var projectName: TextView
        var taskNameValue: TextView
        var editImageView: AppCompatImageView
        var allottedByValue: TextView
        var createdDateValue: TextView
        var estimateCompleteDateValue: TextView

        init {
            projectName = itemView.findViewById(R.id.projectName)
            taskNameValue = itemView.findViewById(R.id.taskNameValue)
            allottedByValue = itemView.findViewById(R.id.allottedByValue)
            createdDateValue = itemView.findViewById(R.id.createdDateValue)
            estimateCompleteDateValue = itemView.findViewById(R.id.estimateCompleteDateValue)
            editImageView = itemView.findViewById(R.id.edit)
            editImageView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {

            }
        }
    }
}