package com.androidx.helpdesk.quickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener
import com.androidx.helpdesk.quickProject.model.GridListModel

class GridListAdapter(private val context: Context?, private val gridList: List<GridListModel>, private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<GridListAdapter.ConnectionsHolder>() {
    var gridListModel: GridListModel? = null
    private var  onClickListenerGridList: OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridListAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_grid_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: GridListAdapter.ConnectionsHolder, position: Int) {
        gridListModel = gridList[position]

        holder.tasksName.text = gridListModel!!.gridTaskName
        holder.taskDateValue.text = gridListModel!!.gridTaskDate
        holder.sequenceValue.text = gridListModel!!.gridSequence.toString()
        holder.assignedByValue.text = gridListModel!!.gridAssignedBy
        holder.assignedToValue.text = gridListModel!!.gridAssignedTo
        holder.taskTypeValue.text = gridListModel!!.gridTaskType
        holder.hoursValue.text = gridListModel!!.gridTaskHours.toString()
        holder.taskStatusValue.text = gridListModel!!.gridTaskStatus
        holder.startDateValue.text = gridListModel!!.gridStartDate
        holder.endDateValue.text = gridListModel!!.gridEndDate

        holder.checkBox.setOnClickListener{
            OnProgrammerClickListener.onProgrammerItemClicked(position)
        }

        holder.delete.setOnClickListener {
            if (onClickListenerGridList != null)
            {
                gridListModel = gridList[position]
                onClickListenerGridList!!.onClick("delete",position, gridListModel!!.gridProjectId, gridListModel!!.gridModuleId, gridListModel!!.gridProjectTaskId, gridListModel!!.gridProjectTaskAllotId)
            }
        }
        holder.edit.setOnClickListener {
            if (onClickListenerGridList != null)
            {
                gridListModel = gridList[position]
                onClickListenerGridList!!.onClick("edit",position, gridListModel!!.gridProjectId, gridListModel!!.gridModuleId, gridListModel!!.gridProjectTaskId, gridListModel!!.gridProjectTaskAllotId)
            }
        }




    }

    override fun getItemCount(): Int {
        return gridList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListenerGridList = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String, position: Int, model: Int, gridModuleId: Int, gridProjectTaskId: Int, gridProjectTaskAllotId: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
         var tasksName: TextView
         var taskDateValue: TextView
         var sequenceValue: TextView
         var assignedByValue: TextView
         var assignedToValue: TextView
         var taskTypeValue: TextView
         var hoursValue: TextView
         var taskStatusValue: TextView
         var startDateValue: TextView
         var endDateValue: TextView
         var checkBox: CheckBox
         var edit: AppCompatImageView
         var delete: AppCompatImageView

        init {
            tasksName = itemView.findViewById(R.id.taskName)
            taskDateValue = itemView.findViewById(R.id.taskDateValue)
            sequenceValue = itemView.findViewById(R.id.sequenceValue)
            assignedByValue = itemView.findViewById(R.id.assignedByValue)
            assignedToValue = itemView.findViewById(R.id.assignedToValue)
            taskTypeValue = itemView.findViewById(R.id.taskTypeValue)
            hoursValue = itemView.findViewById(R.id.hoursValue)
            taskStatusValue = itemView.findViewById(R.id.taskStatusValue)
            startDateValue = itemView.findViewById(R.id.startDateValue)
            endDateValue = itemView.findViewById(R.id.endDateValue)
            edit = itemView.findViewById(R.id.edit)
            delete = itemView.findViewById(R.id.delete)
            checkBox = itemView.findViewById(R.id.checkBox)
        }
    }
}