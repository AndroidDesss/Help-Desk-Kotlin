package com.androidx.helpdesk.projects.adapter

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
import com.androidx.helpdesk.backLog.model.BackLogModel
import com.androidx.helpdesk.editQuickProject.view.QuickEditModule
import com.androidx.helpdesk.editQuickProject.view.QuickEditProject
import com.androidx.helpdesk.projects.model.ProjectModel

class ProjectAdapter(private val context: Context?, private var projectModelList: List<ProjectModel>) : RecyclerView.Adapter<ProjectAdapter.ConnectionsHolder>() {
    var projectModel: ProjectModel? = null
    private var  onClickListener: OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_project, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: ProjectAdapter.ConnectionsHolder, position: Int) {
        projectModel = projectModelList[position]
        holder.taskHeadingName.text = projectModel!!.projectName
        holder.projectTypeValue.text = projectModel!!.projectType
        holder.currentTaskValue.text = projectModel!!.currentTask

        holder.deleteImageView.setOnClickListener {
            if (onClickListener != null)
            {
                projectModel = projectModelList[position]
                onClickListener!!.onClick("delete",position, projectModel!!.projectId)
            }
        }

    }

    override fun getItemCount(): Int {
        return projectModelList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String,position: Int, model: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var taskHeadingName: TextView
        var projectTypeValue: TextView
        var currentTaskValue: TextView
        var directModule: AppCompatImageView
        var editImageView: AppCompatImageView
        var deleteImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            projectTypeValue = itemView.findViewById(R.id.projectTypeValue)
            currentTaskValue = itemView.findViewById(R.id.currentTaskValue)
            directModule = itemView.findViewById(R.id.directModule)
            editImageView = itemView.findViewById(R.id.edit)
            deleteImageView = itemView.findViewById(R.id.delete)
            directModule.setOnClickListener(this)
            editImageView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
                val skillModel = projectModelList[adapterPosition]
                val mIntent = Intent(context, QuickEditProject::class.java)
                mIntent.putExtra("projectId", skillModel.projectId)
                context!!.startActivity(mIntent)
            }
            else if (v === directModule) {
                val skillModel = projectModelList[adapterPosition]
                val mIntent = Intent(context, QuickEditModule::class.java)
                mIntent.putExtra("projectId", skillModel.projectId)
                mIntent.putExtra("projectName", skillModel.projectName)
                context!!.startActivity(mIntent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<ProjectModel>) {
        projectModelList = filterlist
        notifyDataSetChanged()
    }
}