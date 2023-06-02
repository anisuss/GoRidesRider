package com.goridesnigeria.gorides.activities.Repo

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.gorides.model.MesssageClass
import com.goridesnigeria.goridesrider.model.GoRideAppplication
import com.goridesnigeria.goridesrider.utils.HelperClass


class messages {

//    var rideStatus = RideRequestStatusModel()
    var driverId: String = ""
    val helperClass = HelperClass()


    @Volatile private var INSTANCE : messages ?= null
    fun getInstance():messages {

        return INSTANCE ?: synchronized(this) {
            val instance = messages()
            INSTANCE = instance
            instance
        }
    }

    fun loadmessages(messagelist: MutableLiveData<List<MesssageClass>>){

        var objectt = helperClass.getRequestUserDAO(GoRideAppplication.instance)!!
            .getRequestObject(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toInt())
        val prefs: SharedPreferences = GoRideAppplication.instance.getSharedPreferences("UserObject", Context.MODE_PRIVATE)
        driverId = prefs.getString("driverID",objectt.passengerID).toString()

        val databasereference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(driverId)

//        Log.d(TAG,"Driver ID" + driverId)

        databasereference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                rideStatus = snapshot.getValue(RideRequestStatusModel::class.java)!!
                try {
                    val _messages : List<MesssageClass> = snapshot.children.map {
                            dataSnapshot ->
                        dataSnapshot.getValue(MesssageClass::class.java)!!
                    }
                    messagelist.postValue(_messages)

                    Log.d(TAG,"Messages"+_messages)
                }
                catch (e: Exception){
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


//    val userObject = GoRideAppplication.instance.getSharedPreferences("UserObject", Context.MODE_PRIVATE)
    val database = Firebase.database
//    val myRef = database.getReference().child("RideRequest").child(
//        userObject.getString(Constants.id, "")!!)

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
//                rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                Log.d(
//                    "Hamza Zulfiqar",
//                    "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                )
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

}