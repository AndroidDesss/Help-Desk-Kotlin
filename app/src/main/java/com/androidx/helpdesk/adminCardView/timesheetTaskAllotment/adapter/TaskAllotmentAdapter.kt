package com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model.TaskAllotmentTimeSheetModel

class TaskAllotmentAdapter(private val context: Context?, private val taskAllotmentTimeSheetList: List<TaskAllotmentTimeSheetModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val noTask = 1
    private val haveTask = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            noTask  -> {
                NoTaskHolder(layoutInflater.inflate(R.layout.adapter_task_allotment_timesheet_no_task, parent, false))
            }
            haveTask ->  {
                HaveTaskHolder(layoutInflater.inflate(R.layout.adapter_task_allotment_timesheet_have_task, parent, false))
            }
            else -> NoTaskHolder(layoutInflater.inflate(R.layout.adapter_task_allotment_timesheet_no_task, parent, false)) //Generic return

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return taskAllotmentTimeSheetList.size
    }

    override fun getItemViewType(position: Int): Int
    {
        val message: TaskAllotmentTimeSheetModel = taskAllotmentTimeSheetList[position]
        return if (message.projectTaskId!! == 0 )
        {
            noTask
        } else {
            haveTask
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: TaskAllotmentTimeSheetModel = taskAllotmentTimeSheetList[position]
        when (holder.itemViewType) {
            noTask -> (holder as NoTaskHolder).bind(context!!)
            haveTask -> (holder as HaveTaskHolder).bind(message,context!!)
        }
    }

    private class NoTaskHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tastStatus: TextView

        init {
            tastStatus = itemView.findViewById(R.id.tastStatus)
        }

        @SuppressLint("SetTextI18n")
        fun bind(context:Context)
        {
            tastStatus.text="No Task Allotted..!"
        }
    }

    private class HaveTaskHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projectName: TextView
        var moduleName: TextView
        var taskName: TextView
        var allotedHours: TextView
        var taskStatus: TextView

        init {
            projectName = itemView.findViewById(R.id.appName)
            moduleName = itemView.findViewById(R.id.workStatus)
            taskName = itemView.findViewById(R.id.taskNameValue)
            allotedHours = itemView.findViewById(R.id.allotedHourValue)
            taskStatus = itemView.findViewById(R.id.taskStatusValue)
        }

        @SuppressLint("SetTextI18n")
        fun bind(taskAllotmentTimeSheetModel: TaskAllotmentTimeSheetModel, context:Context)
        {
            projectName.text=taskAllotmentTimeSheetModel.projectName
            moduleName.text=taskAllotmentTimeSheetModel.moduleName
            taskName.text=taskAllotmentTimeSheetModel.taskName
            allotedHours.text= taskAllotmentTimeSheetModel.allottedHours.toString()
            taskStatus.text=taskAllotmentTimeSheetModel.taskStatus
        }
    }
}