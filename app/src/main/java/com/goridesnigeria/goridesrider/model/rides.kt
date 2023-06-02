package com.goridesnigeria.goridesrider.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.goridesnigeria.gorides.model.RidesClass
import com.goridesnigeria.goridesrider.utils.HelperClass

class rides{

    val helperClass = HelperClass()

//    val driverid = helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toString()

    var objectt = helperClass.getRequestUserDAO(GoRideAppplication.instance)!!
    .getRequestObject(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId())

//    var objectt = helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toInt()
    val databasereference: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference("RideHistoryDriver").child(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toString())

    @Volatile private var INSTANCE : rides ?= null
    fun getInstance():rides {
        return INSTANCE ?: synchronized(this) {
            val instance = rides()
            INSTANCE = instance
            instance
        }
    }

    fun loadrides(rideslist: MutableLiveData<List<RidesClass>>){

        databasereference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _ride : List<RidesClass> = snapshot.children.map {
                            dataSnapshot ->
                        dataSnapshot.getValue(RidesClass::class.java)!!
                    }

                    rideslist.postValue(_ride)

                }
                catch (e: Exception){

                }


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}