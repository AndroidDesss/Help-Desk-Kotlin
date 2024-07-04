package com.androidx.helpdesk.board.adapter

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
import com.androidx.helpdesk.board.model.BoardModel
import com.androidx.helpdesk.board.view.BoardMembersScreen
import com.androidx.helpdesk.board.view.BoardSprintScreen
import com.androidx.helpdesk.board.view.EditBoardScreen

class BoardAdapter(private val context: Context?, private var boardModelList: List<BoardModel>) : RecyclerView.Adapter<BoardAdapter.ConnectionsHolder>() {
    var boardModel: BoardModel? = null
    private var  onClickListener: BoardAdapter.OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_board_list, parent, false)
        return ConnectionsHolder(v)
    }

    override fun onBindViewHolder(holder: ConnectionsHolder, position: Int) {
        boardModel = boardModelList[position]
        holder.taskHeadingName.text = boardModel!!.projectName
        holder.boardName.text = boardModel!!.boardName


        holder.deleteImageView.setOnClickListener {
            if (onClickListener != null)
            {
                boardModel = boardModelList[position]
                onClickListener!!.onClick("delete",position, boardModel!!.boardId!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return boardModelList.size
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
        var editImageView: AppCompatImageView
        var addBoardMembers: AppCompatImageView
        var sprintBtn: AppCompatImageView
        var deleteImageView: AppCompatImageView

        init {
            taskHeadingName = itemView.findViewById(R.id.taskHeadingName)
            boardName = itemView.findViewById(R.id.boardValue)

            editImageView = itemView.findViewById(R.id.edit)
            addBoardMembers = itemView.findViewById(R.id.addBoardMembers)
            sprintBtn = itemView.findViewById(R.id.sprints)
            deleteImageView = itemView.findViewById(R.id.delete)

            editImageView.setOnClickListener(this)
            deleteImageView.setOnClickListener(this)
            addBoardMembers.setOnClickListener(this)
            sprintBtn.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === editImageView) {
                boardModel = boardModelList[position]
                val mIntent = Intent(context, EditBoardScreen::class.java)
                mIntent.putExtra("boardId", boardModel!!.boardId)
                context!!.startActivity(mIntent)
            }
            else if(v === addBoardMembers)
            {
                boardModel = boardModelList[position]
                val mIntent = Intent(context, BoardMembersScreen::class.java)
                mIntent.putExtra("projectId", boardModel!!.projectID)
                mIntent.putExtra("boardId", boardModel!!.boardId)
                context!!.startActivity(mIntent)
            }
            else if(v === sprintBtn)
            {
                val mIntent = Intent(context, BoardSprintScreen::class.java)
                context!!.startActivity(mIntent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<BoardModel>) {
        boardModelList = filterlist
        notifyDataSetChanged()
    }
}