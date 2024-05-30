package com.androidx.helpdesk.editQuickProject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.editQuickProject.model.EditModuleModel
import com.androidx.helpdesk.editQuickProject.view.QuickEditSubModule
import com.androidx.helpdesk.timeSheet.view.TimeSheetDetails


class EditModuleAdapter(private val context: Context?, private val moduleList: List<EditModuleModel>) : RecyclerView.Adapter<EditModuleAdapter.ConnectionsHolder>() {
    private var moduleModel: EditModuleModel? = null
    private var  onClickListener: OnClickListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditModuleAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_module_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: EditModuleAdapter.ConnectionsHolder, position: Int) {
        moduleModel = moduleList[position]

        val separatedStartDate: Array<String> = moduleModel!!.moduleStartDate!!.split("T").toTypedArray()
        val separatedEndDate: Array<String> = moduleModel!!.moduleEndDate!!.split("T").toTypedArray()

        holder.moduleName.text = moduleModel!!.moduleName
        holder.moduleStartDate.text =  separatedStartDate[0]
        holder.moduleEndDate.text = separatedEndDate[0]
        holder.moduleEstimatedDate.text = moduleModel!!.moduleEstimatedHours.toString()

        holder.moduleDelete.setOnClickListener {
            if (onClickListener != null)
            {
                moduleModel = moduleList[position]
                onClickListener!!.onClick("delete",position, moduleModel!!.moduleId)
            }
        }
        holder.moduleEdit.setOnClickListener {
            if (onClickListener != null)
            {
                moduleModel = moduleList[position]
                onClickListener!!.onClick("edit",position, moduleModel!!.moduleId)
            }
        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(btnName: String,position: Int, model: Int)
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var moduleName: TextView
        var moduleStartDate: TextView
        var moduleEndDate: TextView
        var moduleEstimatedDate: TextView
        var moduleEdit: AppCompatImageView
        var moduleDelete: AppCompatImageView

        init {
            moduleName = itemView.findViewById(R.id.moduleName)
            moduleStartDate = itemView.findViewById(R.id.startDateValue)
            moduleEndDate = itemView.findViewById(R.id.endDateValue)
            moduleEstimatedDate = itemView.findViewById(R.id.estimatedHoursValue)
            moduleEdit = itemView.findViewById(R.id.edit)
            moduleDelete = itemView.findViewById(R.id.delete)
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            if (v === itemView) {
                val moduleModel = moduleList[position]
                val mIntent = Intent(context, QuickEditSubModule::class.java)
                mIntent.putExtra("projectId", moduleModel!!.projectId)
                mIntent.putExtra("projectName", moduleModel!!.projectName)
                mIntent.putExtra("bundleModuleId", moduleModel!!.moduleId)
                context!!.startActivity(mIntent)
            }
        }
    }
}