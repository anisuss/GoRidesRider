package com.goridesnigeria.goridesrider.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.goridesnigeria.goridesrider.utils.HelperClass

class transaction {

    val helperClass = HelperClass()

    @Volatile private var INSTANCE : transaction ?= null
    fun getInstance():transaction {
        return INSTANCE ?: synchronized(this) {
            val instance = transaction()
            INSTANCE = instance
            instance
        }
    }

    fun loadtransaction(transactionlist: MutableLiveData<List<TransactionClass>>){

        val databasereference: DatabaseReference = FirebaseDatabase.getInstance()
            .getReference("DriverPaymentReceipt").child(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toString())

        databasereference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _transaction : List<TransactionClass> = snapshot.children.map {
                            dataSnapshot ->
                        dataSnapshot.getValue(TransactionClass::class.java)!!
                    }
                    transactionlist.postValue(_transaction)
                }
                catch (e: Exception){
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}