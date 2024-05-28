package com.androidx.helpdesk.sprint.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.sprint.model.PendingTaskModel


class PendingTaskAdapter(private val context: Context?, private var pendingTaskModelList: List<PendingTaskModel>, private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<PendingTaskAdapter.ConnectionsHolder>(){
    var pendingTaskModel: PendingTaskModel? = null
    private var  onClickListener: PendingTaskAdapter.OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_pending_task_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: PendingTaskAdapter.ConnectionsHolder, position: Int) {
        pendingTaskModel = pendingTaskModelList[position]

        holder.taskHeadingName.text = pendingTaskModel!!.taskName
        holder.projectName.text = pendingTaskModel!!.projectName
        holder.taskCategory.text = pendingTaskModel!!.description
        holder.assignedTo.text = pendingTaskModel!!.empName
        holder.allottedHours.text = pendingTaskModel!!.allottedHours.toString()
        holder.sequence.text = pendingTaskModel!!.sequence.toString()
        val deliveryDateArray = pendingTaskModel!!.taskDate!!.split("T").toTypedArray()
        holder.taskDate.text = deliveryDateArray[0]

        holder.selectedId.setOnClickListener{
            OnProgrammerClickListener.onProgrammerItemClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return pendingTaskModelList.size

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String,position: Int, model: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskHeadingName: TextView
        var projectName: TextView
        var taskCategory: TextView
        var assignedTo: TextView
        var allottedHours: TextView
        var sequence: TextView
        var taskDate: TextView
        var selectedId: CheckBox

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            projectName = itemView.findViewById(R.id.projectValue)
            taskCategory = itemView.findViewById(R.id.taskCategoryValue)
            assignedTo = itemView.findViewById(R.id.assignedToValue)
            allottedHours = itemView.findViewById(R.id.allottedHoursValue)
            sequence = itemView.findViewById(R.id.sequenceValue)
            taskDate = itemView.findViewById(R.id.taskDateValue)
            selectedId = itemView.findViewById(R.id.checkBox)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<PendingTaskModel>) {
        pendingTaskModelList = filterlist
        notifyDataSetChanged()
    }
}