package com.goridesnigeria.gorides.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.goridesnigeria.gorides.model.RidesClass
import com.goridesnigeria.goridesrider.R


class RidesAdapter:RecyclerView.Adapter<RidesAdapter.MyViewHolder>() {


    private val RideList = ArrayList<RidesClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_trips,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = RideList[position]
        holder.dateAndTime.text = currentitem.dateAndTime
        holder.pickup.text = currentitem.pickupAddress
        holder.textfare.text = currentitem.fare
        holder.destination.text = currentitem.destinationAddress
        holder.dname.text = currentitem.passengerID

        val context: Context = holder.dname.context

        val String = "https://gorides.ucstestserver.xyz/api/users/" + currentitem.passengerID

        val RequestQueue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.GET, String, null, { response ->
            try {
                val courseName: String = response.getString("name")
                holder.dname.text = courseName

//                Toast.makeText(context, "Response " +courseName, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Log.e("TAG", "RESPONSE IS $error")
//            Toast.makeText(context, "Fail to get response"+error, Toast.LENGTH_SHORT)
//                .show()
        })
        RequestQueue.add(request)
    }

    override fun getItemCount(): Int {

        return RideList.size
    }

    fun updateRideList(RideList: List<RidesClass>){

        this.RideList.clear()
        this.RideList.addAll(RideList)
        notifyDataSetChanged()
    }

    class  MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val dateAndTime : TextView = itemView.findViewById(R.id.dateandtime)
        val pickup : TextView = itemView.findViewById(R.id.pickup)
        val textfare : TextView = itemView.findViewById(R.id.fare)
        val destination: TextView = itemView.findViewById(R.id.destination)
        val dname: TextView = itemView.findViewById(R.id.pname)
    }
}