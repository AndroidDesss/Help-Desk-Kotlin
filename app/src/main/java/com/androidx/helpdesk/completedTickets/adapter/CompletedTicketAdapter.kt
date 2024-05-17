package com.androidx.helpdesk.completedTickets.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.chatScreen.view.ChatScreen
import com.androidx.helpdesk.completedTickets.model.CompletedTicketModel
import com.androidx.helpdesk.ticketDetails.TicketDetails

class CompletedTicketAdapter(private val context: Context?, private val completedTicketModelList: List<CompletedTicketModel>) : RecyclerView.Adapter<CompletedTicketAdapter.ConnectionsHolder>() {
    var completedTicketModel: CompletedTicketModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTicketAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_tickets, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: CompletedTicketAdapter.ConnectionsHolder, position: Int) {
        completedTicketModel = completedTicketModelList[position]
        holder.taskHeadingName.text = completedTicketModel!!.ticketName
        holder.createdOn.text = completedTicketModel!!.ticketCreatedOn?.replace("T", " ") ?: ""
        holder.subject.text = completedTicketModel!!.ticketSubject
        val ticketSeverityValue = completedTicketModel!!.ticketSeverity
        when (ticketSeverityValue) {
            1 -> holder.ticketSeverity.setText(R.string.critical)
            2 -> holder.ticketSeverity.setText(R.string.high)
            3 -> holder.ticketSeverity.setText(R.string.medium)
            4 -> holder.ticketSeverity.setText(R.string.low)
        }
    }

    override fun getItemCount(): Int {
        return completedTicketModelList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        var taskHeadingName: TextView
        var createdOn: TextView
        var subject: TextView
        var ticketSeverity: TextView
        var editImageView: AppCompatImageView
        var chatImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            createdOn = itemView.findViewById(R.id.createdOnValue)
            subject = itemView.findViewById(R.id.subjectValue)
            ticketSeverity = itemView.findViewById(R.id.ticketSeverityValue)
            editImageView = itemView.findViewById(R.id.edit)
            chatImageView = itemView.findViewById(R.id.chat)
            editImageView.setOnClickListener(this)
            chatImageView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
                val skillModel = completedTicketModelList[adapterPosition]
                val mIntent = Intent(context, TicketDetails::class.java)
                mIntent.putExtra("TaskId", skillModel.miscId)
                context!!.startActivity(mIntent)
            }
            else if(v === chatImageView)
            {
                val skillModel = completedTicketModelList[adapterPosition]
                val mIntent = Intent(context, ChatScreen::class.java)
                mIntent.putExtra("TaskId", skillModel.miscId)
                context!!.startActivity(mIntent)
            }
        }
    }
}