package com.androidx.helpdesk.board.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.quickProject.`interface`.OnProgrammerListener

class AvailableBoardMembersAdapter(private val context: Context?, private val availableBoardMembersList: List<AvailableBoardMembersModel>, private val OnProgrammerClickListener: OnProgrammerListener) : RecyclerView.Adapter<AvailableBoardMembersAdapter.ConnectionsHolder>() {
    var availableBoardMemberModel: AvailableBoardMembersModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableBoardMembersAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_available_board_members, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: AvailableBoardMembersAdapter.ConnectionsHolder, position: Int) {
        availableBoardMemberModel = availableBoardMembersList[position]
        holder.employeeName.text = availableBoardMemberModel!!.availableEmployeeName

        holder.employeeId.setOnClickListener{
            OnProgrammerClickListener.onProgrammerItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return availableBoardMembersList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var employeeName: TextView
        var employeeId: CheckBox
        init {
            employeeName = itemView.findViewById(R.id.emplyoeeName)
            employeeId = itemView.findViewById(R.id.checkBox)
        }
    }
}