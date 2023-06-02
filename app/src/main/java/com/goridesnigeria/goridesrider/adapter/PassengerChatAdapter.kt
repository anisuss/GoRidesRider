package com.goridesnigeria.gorides.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goridesnigeria.gorides.model.passengerMessageClass
import com.goridesnigeria.goridesrider.R

class PassengerChatAdapter: RecyclerView.Adapter<PassengerChatAdapter.MyViewHolder>() {

    private val PassengerMessageList = ArrayList<passengerMessageClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerChatAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.passenger_message_layout,parent,false)
        return PassengerChatAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PassengerChatAdapter.MyViewHolder, position: Int) {

        val currentitem = PassengerMessageList[position]
        holder.pmessage.text = currentitem.message
    }

    override fun getItemCount(): Int {

        return PassengerMessageList.size
    }

    fun updatePassengerMessageList(PassengerMessageList: List<passengerMessageClass>){

        this.PassengerMessageList.clear()
        this.PassengerMessageList.addAll(PassengerMessageList)
        notifyDataSetChanged()
    }

    class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val pmessage: TextView = itemView.findViewById(R.id.pmessage)
    }
}