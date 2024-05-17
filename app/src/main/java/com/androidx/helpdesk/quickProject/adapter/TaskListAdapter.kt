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
import com.androidx.helpdesk.quickProject.model.TaskListModel

class TaskListAdapter(private val context: Context?, private val taskList: List<TaskListModel>,private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<TaskListAdapter.ConnectionsHolder>() {
    var taskListModel: TaskListModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: TaskListAdapter.ConnectionsHolder, position: Int) {
        taskListModel = taskList[position]
        holder.resourceName.text = taskListModel!!.taskResponseName

        holder.resourceId.setOnClickListener{
            OnProgrammerClickListener.onProgrammerItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
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