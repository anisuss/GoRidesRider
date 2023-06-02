package com.goridesnigeria.goridesrider.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.model.TransactionClass

class TransactionAdapter:RecyclerView.Adapter<TransactionAdapter.MyViewHolder>() {

    private val TransactionList = ArrayList<TransactionClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction,parent,false)
        return TransactionAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionAdapter.MyViewHolder, position: Int) {

        val currentitem = TransactionList[position]
        holder.amount.text = currentitem.Amount
        holder.dateandtime.text = currentitem.DateAndTime
        holder.name.text = currentitem.passengerName
        holder.status.text  = currentitem.Status
    }

    override fun getItemCount(): Int {

        return TransactionList.size
    }

    fun updateTransactionList(TransactionList: List<TransactionClass>){

        this.TransactionList.clear()
        this.TransactionList.addAll(TransactionList)
        notifyDataSetChanged()
    }

    class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val amount: TextView = itemView.findViewById(R.id.amount)
        val dateandtime: TextView = itemView.findViewById(R.id.date)
        val name: TextView = itemView.findViewById(R.id.name)
        val status: TextView = itemView.findViewById(R.id.statustext)
    }

}