package com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model.EmployeeTaskAllotmentModel
import com.androidx.helpdesk.adminCardView.timesheetTaskAllotment.model.TaskAllotmentTimeSheetModel

class TaskAllotmentTimeSheetAdapter(private val context: Context?, private var taskAllotmentTimeSheetList: List<EmployeeTaskAllotmentModel>) : RecyclerView.Adapter<TaskAllotmentTimeSheetAdapter.ConnectionsHolder>() {
    var taskAllotmentTimeSheetModel: EmployeeTaskAllotmentModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_task_allotment_timesheet, parent, false)
        return ConnectionsHolder(v)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        taskAllotmentTimeSheetModel = taskAllotmentTimeSheetList[position]
        val taskAllotmentList = mutableListOf<TaskAllotmentTimeSheetModel>()
        val timesheetList = mutableListOf<TaskAllotmentTimeSheetModel>()
        taskAllotmentList.clear()
        timesheetList.clear()
        holder.employeeName.text = taskAllotmentTimeSheetModel!!.fullName
        holder.departmentName.text = taskAllotmentTimeSheetModel!!.department

        for (department in taskAllotmentTimeSheetModel!!.departmentData)
        {
            if (department.taskRowCount!! >= 0 && department.taskRowCount!! != 10)
            {
                taskAllotmentList.add(department)
            }
            else if(department.taskRowCount == 10 && department.timesheetRowCount!! >= 0)
            {
                timesheetList.add(department)
            }
        }

        if (taskAllotmentList.isNotEmpty())
        {
            val taskAllotmentAdapter = TaskAllotmentAdapter(context, taskAllotmentList)
            holder.taskAllotmentRecyclerView.adapter = taskAllotmentAdapter
            taskAllotmentAdapter.notifyDataSetChanged()
        }

        if (timesheetList.isNotEmpty())
        {
            val timesheetAdapter = TaskAllotmentAdapter(context, timesheetList)
            holder.timesheetRecyclerView.adapter = timesheetAdapter
            timesheetAdapter.notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return taskAllotmentTimeSheetList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var employeeName: TextView
        var departmentName: TextView
        var taskAllotmentArrow: AppCompatImageView
        var timesheetArrow: AppCompatImageView
        var taskAllotmentRecyclerView: RecyclerView
        var timesheetRecyclerView: RecyclerView
        var taskAllotmentArrowStatus = 0
        var timesheetStatus = 0

        init {
            employeeName = itemView.findViewById(R.id.employeeName)
            departmentName = itemView.findViewById(R.id.departmentName)
            taskAllotmentArrow = itemView.findViewById(R.id.taskAllotmentArrow)
            timesheetArrow = itemView.findViewById(R.id.timesheetArrow)
            taskAllotmentRecyclerView = itemView.findViewById(R.id.taskAllotmentRecyclerView)
            timesheetRecyclerView = itemView.findViewById(R.id.timesheetRecyclerView)
            taskAllotmentArrow.setOnClickListener(this)
            timesheetArrow.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === taskAllotmentArrow)
            {
                if(taskAllotmentArrowStatus == 0)
                {
                    taskAllotmentArrowStatus = 1
                    taskAllotmentRecyclerView.visibility = View.VISIBLE
                    taskAllotmentArrow.setBackgroundResource(R.drawable.up_arrow)
                }
                else
                {
                    taskAllotmentArrowStatus = 0
                    taskAllotmentRecyclerView.visibility = View.GONE
                    taskAllotmentArrow.setBackgroundResource(R.drawable.down_arrow)
                }
            }
            else if (v === timesheetArrow)
            {
                if(timesheetStatus == 0)
                {
                    timesheetStatus = 1
                    timesheetRecyclerView.visibility = View.VISIBLE
                    timesheetArrow.setBackgroundResource(R.drawable.up_arrow)
                }
                else
                {
                    timesheetStatus = 0
                    timesheetRecyclerView.visibility = View.GONE
                    timesheetArrow.setBackgroundResource(R.drawable.down_arrow)
                }
            }
        }
    }
}