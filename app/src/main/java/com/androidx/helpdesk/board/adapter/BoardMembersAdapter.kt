package com.androidx.helpdesk.board.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.board.model.BoardMembersModel
import com.androidx.helpdesk.quickProject.`interface`.OnDeleteListener


class BoardMembersAdapter(private val context: Context?, private val boardMembersList: List<BoardMembersModel>, private val OnDeleteClickListener: OnDeleteListener) : RecyclerView.Adapter<BoardMembersAdapter.ConnectionsHolder>() {
    var boardMemberModel: BoardMembersModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMembersAdapter.ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_board_members, parent, false)
        return ConnectionsHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BoardMembersAdapter.ConnectionsHolder, position: Int) {
        boardMemberModel = boardMembersList[position]
        holder.resourceName.text = boardMemberModel!!.boardMemberName
        holder.resourceRole.text = "Role: " + boardMemberModel!!.boardMemberRole

        holder.resourceId.setOnClickListener{
            OnDeleteClickListener.onDeleteItemClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return boardMembersList.size
    }

    inner class ConnectionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var resourceName: TextView
        var resourceRole: TextView
        var resourceId: CheckBox
        init {
            resourceName = itemView.findViewById(R.id.resourceName)
            resourceRole = itemView.findViewById(R.id.resourceRole)
            resourceId = itemView.findViewById(R.id.checkBox)
        }
    }
}