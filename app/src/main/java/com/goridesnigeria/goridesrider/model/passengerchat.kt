package com.goridesnigeria.gorides.activities.Repo

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.goridesnigeria.gorides.model.passengerMessageClass
import com.goridesnigeria.goridesrider.data.model.UserModel
import com.goridesnigeria.goridesrider.model.GoRideAppplication
import com.goridesnigeria.goridesrider.utils.HelperClass

class passengerchat {

    val userModel = UserModel()
    val helperClass = HelperClass()

    @Volatile private var INSTANCE : passengerchat ?= null
    fun getInstance():passengerchat {
        return INSTANCE ?: synchronized(this) {
            val instance = passengerchat()
            INSTANCE = instance
            instance
        }
    }

    fun loadpassengerMessages(rideslist: MutableLiveData<List<passengerMessageClass>>){

        val databasereference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("Chats").child(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toString())

        databasereference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _message : List<passengerMessageClass> = snapshot.children.map {
                            dataSnapshot ->
                        dataSnapshot.getValue(passengerMessageClass::class.java)!!
                    }

                    rideslist.postValue(_message)

                }
                catch (e: Exception){

                }

            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}