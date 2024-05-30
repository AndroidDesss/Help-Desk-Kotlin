package com.androidx.helpdesk.sprint.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.sprint.model.SprintModel
import com.androidx.helpdesk.sprint.view.EditSprintScreen
import com.androidx.helpdesk.sprint.view.SprintDetailsTabScreen

class CurrentSprintAdapter(private val context: Context?, private var sprintModelList: List<SprintModel>) : RecyclerView.Adapter<CurrentSprintAdapter.ConnectionsHolder>(){
    var sprintModel: SprintModel? = null
    private var  onClickListener: CurrentSprintAdapter.OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int):ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_current_sprint, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: CurrentSprintAdapter.ConnectionsHolder, position: Int) {
        sprintModel = sprintModelList[position]
        val startDateArray = sprintModel!!.sprintStartDate!!.split("T").toTypedArray()
        holder.startDate.text = startDateArray[0]

        val endDateArray = sprintModel!!.sprintEndDate!!.split("T").toTypedArray()
        holder.endDate.text = endDateArray[0]

        val deliveryDateArray = sprintModel!!.sprintDeliveryDate!!.split("T").toTypedArray()
        holder.deliveryDate.text = deliveryDateArray[0]

        holder.taskHeadingName.text = sprintModel!!.projectName
        holder.boardName.text = sprintModel!!.boardName
        holder.sprintName.text = sprintModel!!.sprintName
        holder.estimateHours.text = sprintModel!!.sprintEstimatedHours

        holder.deleteImageView.setOnClickListener {
            if (onClickListener != null)
            {
                sprintModel = sprintModelList[position]
                onClickListener!!.onClick("delete",position, sprintModel!!.sprintId!!)
            }
        }

    }

    override fun getItemCount(): Int {
        return sprintModelList.size

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
        var boardName: TextView
        var sprintName: TextView
        var startDate: TextView
        var endDate: TextView
        var deliveryDate: TextView
        var estimateHours: TextView
        var editImageView: AppCompatImageView
        var deleteImageView: AppCompatImageView
        var allotTaskToSprintImageView: AppCompatImageView
        var burnOutChartImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            boardName = itemView.findViewById(R.id.boardValue)
            sprintName = itemView.findViewById(R.id.sprintNameValue)
            startDate = itemView.findViewById(R.id.startDateValue)
            endDate = itemView.findViewById(R.id.endDateValue)
            deliveryDate = itemView.findViewById(R.id.deliveryDateValue)
            estimateHours = itemView.findViewById(R.id.estimateValue)
            editImageView = itemView.findViewById(R.id.edit)
            deleteImageView = itemView.findViewById(R.id.delete)
            allotTaskToSprintImageView = itemView.findViewById(R.id.allotTaskToSprint)
            burnOutChartImageView = itemView.findViewById(R.id.burnOutChart)
            editImageView.setOnClickListener(this)
            deleteImageView.setOnClickListener(this)
//            allotTaskToSprintImageView.setOnClickListener(this)
            burnOutChartImageView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
                val sprintModel = sprintModelList[position]
                val mIntent = Intent(context, EditSprintScreen::class.java)
                mIntent.putExtra("sprintId", sprintModel . sprintId)
                context!!.startActivity(mIntent)
            }
            else if (v === allotTaskToSprintImageView) {
                val sprintModel = sprintModelList[position]
                val mIntent = Intent(context, SprintDetailsTabScreen::class.java)
                mIntent.putExtra("sprintId", sprintModel.sprintId)
                mIntent.putExtra("projectId", sprintModel.projectId)
                context!!.startActivity(mIntent)
            }
            if (v === burnOutChartImageView) {

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<SprintModel>) {
        sprintModelList = filterlist
        notifyDataSetChanged()
    }
}