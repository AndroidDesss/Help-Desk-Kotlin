package com.androidx.helpdesk.backLog.adapter

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


class BackLogAdapter(private val context: Context?, private val backLogModelList: List<BackLogModel>) : RecyclerView.Adapter<BackLogAdapter.ConnectionsHolder>() {
    var backLogModel: BackLogModel? = null
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
    }

    override fun getItemCount(): Int {
        return backLogModelList.size
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
        var deleteImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            projectName = itemView.findViewById(R.id.projectValue)
            moduleName = itemView.findViewById(R.id.moduleValue)
            startDate = itemView.findViewById(R.id.startDateValue)
            taskStatus = itemView.findViewById(R.id.taskStatusValue)
            priority = itemView.findViewById(R.id.priorityValue)
            editImageView = itemView.findViewById(R.id.edit)
            deleteImageView = itemView.findViewById(R.id.delete)
            editImageView.setOnClickListener(this)
            deleteImageView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
//                val skillModel = closedTicketModelList[adapterPosition]
//                val mIntent = Intent(context, TicketDetails::class.java)
//                mIntent.putExtra("TaskId", skillModel.miscId)
//                context!!.startActivity(mIntent)
            }
            else if(v === deleteImageView)
            {
//                val skillModel = closedTicketModelList[adapterPosition]
//                val mIntent = Intent(context, ChatScreen::class.java)
//                mIntent.putExtra("TaskId", skillModel.miscId)
//                context!!.startActivity(mIntent)
            }
        }
    }
}