package com.androidx.helpdesk.sprint.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.sprint.model.SprintModel
import com.androidx.helpdesk.sprint.view.EditSprintScreen

class PendingSprintAdapter(private val context: Context?, private var sprintModelList: List<SprintModel>) : RecyclerView.Adapter<PendingSprintAdapter.ConnectionsHolder>(){

    var sprintModel: SprintModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_pending_sprint, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: PendingSprintAdapter.ConnectionsHolder, position: Int) {
        sprintModel = sprintModelList[position]
        val startDateArray = sprintModel!!.sprintStartDate!!.split("T").toTypedArray()
        holder.startDate.text = startDateArray[0]

        val endDateArray = sprintModel!!.sprintEndDate!!.split("T").toTypedArray()
        holder.endDate.text = endDateArray[0]

        val deliveryDateArray = sprintModel!!.sprintDeliveryDate!!.split("T").toTypedArray()
        holder.deliveryDate.text = deliveryDateArray[0]

        holder.taskHeadingName.text = sprintModel!!.projectName
        holder.boardName.text = sprintModel!!.boardName
        holder.sprintName.text = sprintModel!!.sprintName
        holder.estimateHours.text = sprintModel!!.sprintEstimatedHours

    }

    override fun getItemCount(): Int {
        return sprintModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var taskHeadingName: TextView
        var boardName: TextView
        var sprintName: TextView
        var startDate: TextView
        var endDate: TextView
        var deliveryDate: TextView
        var estimateHours: TextView
        var allotTaskToSprint: AppCompatImageView
        var burnOutChart: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            boardName = itemView.findViewById(R.id.boardValue)
            sprintName = itemView.findViewById(R.id.sprintNameValue)
            startDate = itemView.findViewById(R.id.startDateValue)
            endDate = itemView.findViewById(R.id.endDateValue)
            deliveryDate = itemView.findViewById(R.id.deliveryDateValue)
            estimateHours = itemView.findViewById(R.id.estimateValue)
            allotTaskToSprint = itemView.findViewById(R.id.allotTaskToSprint)
            burnOutChart = itemView.findViewById(R.id.burnOutChart)
            allotTaskToSprint.setOnClickListener(this)
            burnOutChart.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === allotTaskToSprint) {

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<SprintModel>) {
        sprintModelList = filterlist
        notifyDataSetChanged()
    }
}