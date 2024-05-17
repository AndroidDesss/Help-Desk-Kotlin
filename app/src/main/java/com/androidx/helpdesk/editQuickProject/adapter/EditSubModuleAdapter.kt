package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.model.EditSubModuleModel


class EditSubModuleAdapter(private val context: Context?, private val subModuleList: List<EditSubModuleModel>) : RecyclerView.Adapter<EditSubModuleAdapter.ConnectionsHolder>() {
    private var subModuleModel: EditSubModuleModel? = null
    private var  onClickListenerSubModule: EditSubModuleAdapter.OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditSubModuleAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sub_module_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: EditSubModuleAdapter.ConnectionsHolder, position: Int) {
        subModuleModel = subModuleList[position]

        holder.subModuleName.text = subModuleModel!!.subModuleName
        holder.subModuleScreenName.text = subModuleModel!!.subModuleTaskName
        holder.subModuleTaskCategory.text = subModuleModel!!.subModuleTaskCategory
        holder.subModuleEstimatedHours.text = subModuleModel!!.subModuleEstimatedHours.toString()
        holder.subModuleTaskStatus.text = subModuleModel!!.subModuleName

        holder.subModuleDelete.setOnClickListener {
            if (onClickListenerSubModule != null)
            {
                subModuleModel = subModuleList[position]
                onClickListenerSubModule!!.onClick("delete",position, subModuleModel!!.subModuleProjectTaskId)
            }
        }
        holder.subModuleEdit.setOnClickListener {
            if (onClickListenerSubModule != null)
            {
                subModuleModel = subModuleList[position]
                onClickListenerSubModule!!.onClick("edit",position, subModuleModel!!.subModuleProjectTaskId)
            }
        }

    }

    override fun getItemCount(): Int {
        return subModuleList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListenerSubModule = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String,position: Int, model: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var subModuleName: TextView
        var subModuleScreenName: TextView
        var subModuleTaskCategory: TextView
        var subModuleEstimatedHours: TextView
        var subModuleTaskStatus: TextView
        var subModuleEdit: AppCompatImageView
        var subModuleDelete: AppCompatImageView

        init {
            subModuleName = itemView.findViewById(R.id.subModuleName)
            subModuleScreenName = itemView.findViewById(R.id.taskNameValue)
            subModuleTaskCategory = itemView.findViewById(R.id.taskCategoryValue)
            subModuleEstimatedHours = itemView.findViewById(R.id.estimatedHoursValue)
            subModuleTaskStatus = itemView.findViewById(R.id.taskStatusValue)
            subModuleEdit = itemView.findViewById(R.id.edit)
            subModuleDelete = itemView.findViewById(R.id.delete)
        }
    }
}