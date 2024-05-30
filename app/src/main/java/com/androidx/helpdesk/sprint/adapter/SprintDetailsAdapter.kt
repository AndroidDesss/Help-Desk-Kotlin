package com.androidx.helpdesk.sprint.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.sprint.model.SprintDetailsModel

class SprintDetailsAdapter(private val context: Context?, private var sprintDetailsModelList: List<SprintDetailsModel>) : RecyclerView.Adapter<SprintDetailsAdapter.ConnectionsHolder>(){
    var sprintDetailsModel: SprintDetailsModel? = null
    private var  onClickListener: SprintDetailsAdapter.OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sprint_details, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: SprintDetailsAdapter.ConnectionsHolder, position: Int) {
        sprintDetailsModel = sprintDetailsModelList[position]

        holder.taskHeadingName.text = sprintDetailsModel!!.taskName
        holder.projectName.text = sprintDetailsModel!!.projectName
        holder.taskCategory.text = sprintDetailsModel!!.description
        holder.assignedTo.text = sprintDetailsModel!!.empName
        holder.allottedHours.text = sprintDetailsModel!!.allottedHours.toString()
        holder.completedHours.text = sprintDetailsModel!!.actualHours.toString()
        val deliveryDateArray = sprintDetailsModel!!.taskDate!!.split("T").toTypedArray()
        holder.taskDate.text = deliveryDateArray[0]

        holder.deleteImageView.setOnClickListener {
            if (onClickListener != null)
            {
                sprintDetailsModel = sprintDetailsModelList[position]
                onClickListener!!.onClick("delete",position, sprintDetailsModel!!.sprintListDetailsId)
            }
        }

    }

    override fun getItemCount(): Int {
        return sprintDetailsModelList.size

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
        var completedHours: TextView
        var taskDate: TextView
        var deleteImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            projectName = itemView.findViewById(R.id.projectValue)
            taskCategory = itemView.findViewById(R.id.taskCategoryValue)
            assignedTo = itemView.findViewById(R.id.assignedToValue)
            allottedHours = itemView.findViewById(R.id.allottedHoursValue)
            completedHours = itemView.findViewById(R.id.completedHoursValue)
            taskDate = itemView.findViewById(R.id.taskDateValue)
            deleteImageView = itemView.findViewById(R.id.delete)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<SprintDetailsModel>) {
        sprintDetailsModelList = filterlist
        notifyDataSetChanged()
    }
}