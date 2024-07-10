package com.androidx.helpdesk.chatScreen.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidx.helpdesk.R
import com.androidx.helpdesk.chatScreen.model.ChatModel
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat


class ChatAdapter(private val context: Context?, private val chatList: List<ChatModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val messageSent = 1
    private val messageReceived = 2
    private var getCurrentDateTime = ""
    private var sameDate=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            messageSent  -> {
                SentMessageHolder(layoutInflater.inflate(R.layout.item_sent_message, parent, false))
            }
            messageReceived ->  {
                ReceivedMessageHolder(layoutInflater.inflate(R.layout.item_received_message, parent, false))
            }
            else -> SentMessageHolder(layoutInflater.inflate(R.layout.item_sent_message, parent, false)) //Generic return

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int
    {
        val message: ChatModel = chatList.get(position)
        return if (message.chatClassType.equals("msg-left"))
        {
             messageSent
        } else {
             messageReceived
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: ChatModel = chatList.get(position)

        val time= message.chatDate
        val datearray = time!!.split("T").toTypedArray()
        val getDate = datearray[0]
        if (getCurrentDateTime.compareTo(getDate) != 0) {
            getCurrentDateTime = getDate
            sameDate="true"
        }
        else
        {
            sameDate="false"
        }
        when (holder.itemViewType) {
            messageSent -> (holder as SentMessageHolder).bind(sameDate,message,context!!)
            messageReceived -> (holder as ReceivedMessageHolder).bind(sameDate,message,context!!)
        }
    }

    private class ReceivedMessageHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var relativeLayout: RelativeLayout
        var receivedMessageTime: TextView
        var receivedMessageContent: TextView
        var receivedProfilePic: ShapeableImageView
        var initialLayout: FrameLayout
        var initialTv: TextView
        var dateTv: TextView

        init {
            receivedMessageTime = itemView.findViewById(R.id.receivedMessageTime)
            receivedMessageContent = itemView.findViewById(R.id.receivedMessageContent)
            receivedProfilePic = itemView.findViewById(R.id.receivedProfilePic)
            initialLayout = itemView.findViewById(R.id.initialLayout)
            initialTv = itemView.findViewById(R.id.initialTv)
            relativeLayout = itemView.findViewById(R.id.relativeLayout)
            dateTv = itemView.findViewById(R.id.dateTv)
        }

        @SuppressLint("SetTextI18n")
        fun bind(date: String, chatModel: ChatModel, context:Context)
        {
            receivedMessageContent.text=chatModel.chatMessage
            val time= chatModel.chatDate
            val datearray = time!!.split("T").toTypedArray()
            val correctDateFormat: Array<String> = datearray[0].split("-").toTypedArray()
            val getTime = datearray[1]

            if (date.equals("true") || date == "true") {
                relativeLayout.visibility=View.VISIBLE
                dateTv.text=correctDateFormat[1]+"-"+correctDateFormat[2]+"-"+correctDateFormat[0]
            }
            else if(date.equals("false") || date == "false")
            {
                relativeLayout.visibility=View.GONE
            }

            receivedMessageTime.text = chatModel.chatUserName +" "+ getTime.substring(0,getTime.length-7)
            if(chatModel.chatImage == "null")
            {
                initialLayout.visibility=View.VISIBLE
                receivedProfilePic.visibility=View.INVISIBLE
                initialTv.text=chatModel.chatInitial
            }
            else
            {
                initialLayout.visibility=View.INVISIBLE
                receivedProfilePic.visibility=View.VISIBLE
                Glide.with(context)
                    .load("http://helpdesk-online.net/"+chatModel.chatImage!!.replace("~/",""))
                    .dontAnimate()
                    .centerCrop().into(receivedProfilePic)
            }
        }
    }

    private class SentMessageHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var relativeLayout: RelativeLayout
        var sentMessageTime: TextView
        var sentMessageContent: TextView
        var sentProfilePic: ShapeableImageView
        var initialLayout: FrameLayout
        var initialTv: TextView
        var dateTv: TextView

        init {
            sentMessageTime = itemView.findViewById(R.id.sentMessageTime)
            sentMessageContent = itemView.findViewById(R.id.sentMessageContent)
            sentProfilePic = itemView.findViewById(R.id.sentProfilePic)
            initialLayout = itemView.findViewById(R.id.initialLayout)
            initialTv = itemView.findViewById(R.id.initialTv)
            relativeLayout = itemView.findViewById(R.id.relativeLayout)
            dateTv = itemView.findViewById(R.id.dateTv)
        }

        @SuppressLint("SetTextI18n")
        fun bind(date: String, chatModel: ChatModel, context:Context)
        {
            sentMessageContent.text=chatModel.chatMessage
            val time= chatModel.chatDate
            val datearray = time!!.split("T").toTypedArray()
            val correctDateFormat: Array<String> = datearray[0].split("-").toTypedArray()
            val getTime = datearray[1]

            if (date.equals("true") || date == "true") {
                relativeLayout.visibility=View.VISIBLE
                dateTv.text=correctDateFormat[1]+"-"+correctDateFormat[2]+"-"+correctDateFormat[0]
            }
            else if(date.equals("false") || date == "false")
            {
                relativeLayout.visibility=View.GONE
            }

            sentMessageTime.text = chatModel.chatUserName +" "+ getTime.substring(0,getTime.length-7)

            if(chatModel.chatImage == "null")
            {
                initialLayout.visibility=View.VISIBLE
                sentProfilePic.visibility=View.INVISIBLE
                initialTv.text=chatModel.chatInitial
            }
            else
            {
                initialLayout.visibility=View.INVISIBLE
                sentProfilePic.visibility=View.VISIBLE
                Glide.with(context)
                    .load("http://helpdesk-online.net/"+chatModel.chatImage!!.replace("~/",""))
                    .dontAnimate()
                    .centerCrop().into(sentProfilePic)
            }
        }
    }
}

