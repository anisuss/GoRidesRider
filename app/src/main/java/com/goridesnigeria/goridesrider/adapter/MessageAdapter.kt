package com.goridesnigeria.gorides.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goridesnigeria.gorides.model.MesssageClass
import com.goridesnigeria.goridesrider.R

class MessageAdapter:RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private val MessageList = ArrayList<MesssageClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_design,parent,false)
        return MessageAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = MessageList[position]
        holder.pmessage.text = currentitem.message
    }

    override fun getItemCount(): Int {

        return MessageList.size
    }

    fun updateMessageList(MessageList: List<MesssageClass>){

        this.MessageList.clear()
        this.MessageList.addAll(MessageList)
        notifyDataSetChanged()
    }

    class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val pmessage: TextView = itemView.findViewById(R.id.pmessage)
    }


}