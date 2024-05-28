package com.androidx.helpdesk.backLog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.backLog.model.BackLogTaskListModel
import com.androidx.helpdesk.editQuickProject.`interface`.OnProgrammerListener


class BackLogTaskListAdapter(private val context: Context?, private val taskList: List<BackLogTaskListModel>, private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<BackLogTaskListAdapter.ConnectionsHolder>() {
    var taskListModel: BackLogTaskListModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackLogTaskListAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: BackLogTaskListAdapter.ConnectionsHolder, position: Int) {
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