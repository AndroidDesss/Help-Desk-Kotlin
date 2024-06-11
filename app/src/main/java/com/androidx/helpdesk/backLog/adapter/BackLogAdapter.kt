package com.androidx.helpdesk.backLog.adapter

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
import com.androidx.helpdesk.backLog.view.BackLogEstimateAllotScreen
import com.androidx.helpdesk.backLog.view.EditBackLogScreen
import java.util.Locale

class BackLogAdapter(private val context: Context?, private var backLogModelList: List<BackLogModel>) : RecyclerView.Adapter<BackLogAdapter.ConnectionsHolder>() {
    var backLogModel: BackLogModel? = null
    private var  onClickListener: BackLogAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_backlog_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        backLogModel = backLogModelList[position]
        holder.taskHeadingName.text = backLogModel!!.taskName
        holder.projectName.text = backLogModel!!.projectName
        holder.moduleName.text = backLogModel!!.moduleName
        val dateArray = backLogModel!!.startDate!!.split("T").toTypedArray()
        holder.startDate.text = dateArray[0]
        holder.taskStatus.text = backLogModel!!.cardViewStatus
        holder.priority.text = backLogModel!!.priority

        holder.deleteImageView.setOnClickListener {
            if (onClickListener != null)
            {
                backLogModel = backLogModelList[position]
                onClickListener!!.onClick("delete",position, backLogModel!!.projectTaskId!!)
            }
        }

        if(backLogModel!!.hideButton == 1)
        {
            holder.estimateAllot.visibility = View.VISIBLE
        }
        else
        {
            holder.estimateAllot.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return backLogModelList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String,position: Int, model: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var taskHeadingName: TextView
        var projectName: TextView
        var moduleName: TextView
        var startDate: TextView
        var taskStatus: TextView
        var priority: TextView
        var editImageView: AppCompatImageView
        var estimateAllot: AppCompatImageView
        var deleteImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            projectName = itemView.findViewById(R.id.projectValue)
            moduleName = itemView.findViewById(R.id.moduleValue)
            startDate = itemView.findViewById(R.id.startDateValue)
            taskStatus = itemView.findViewById(R.id.taskStatusValue)
            priority = itemView.findViewById(R.id.priorityValue)
            editImageView = itemView.findViewById(R.id.edit)
            estimateAllot = itemView.findViewById(R.id.estimateAllot)
            deleteImageView = itemView.findViewById(R.id.delete)
            editImageView.setOnClickListener(this)
            deleteImageView.setOnClickListener(this)
            estimateAllot.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
                val backLogModel = backLogModelList[position]
                val mIntent = Intent(context, EditBackLogScreen::class.java)
                mIntent.putExtra("ProjectTaskId", backLogModel.projectTaskId)
                context!!.startActivity(mIntent)
            }
            else if(v === estimateAllot)
            {
                val backLogModel = backLogModelList[position]
                val mIntent = Intent(context, BackLogEstimateAllotScreen::class.java)
                mIntent.putExtra("projectId", backLogModel.projectID)
                mIntent.putExtra("moduleId", backLogModel.moduleID)
                mIntent.putExtra("projectTaskId", backLogModel.projectTaskId)
                mIntent.putExtra("taskCategoryId", backLogModel.taskCategory)
                mIntent.putExtra("projectName", backLogModel.projectName)
                context!!.startActivity(mIntent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<BackLogModel>) {
        backLogModelList = filterlist
        notifyDataSetChanged()
    }
}