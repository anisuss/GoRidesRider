package com.goridesnigeria.goridesrider

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.goridesrider.data.model.RequestBookingModel
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.databinding.ActivityIncomingRequestBinding
import com.goridesnigeria.goridesrider.model.currentride
import com.goridesnigeria.goridesrider.utils.Constants
import com.goridesnigeria.goridesrider.utils.HelperClass

class IncomingRequestActivity : AppCompatActivity() {

    val helperClass = HelperClass()
    var database = Firebase.database
    var myRef = database.getReference()
    lateinit var binding: ActivityIncomingRequestBinding
    var requestBooking = RequestBookingModel()
    var requestModel = RequestBookingModel()
    val ride = currentride()
    var rideStatus = RideRequestStatusModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@IncomingRequestActivity,
            R.layout.activity_incoming_request)

        Handler().postDelayed({
            RideCondition()
//            finish()
        },3000)

//        checkridestatus()

//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                RideCondition()
//                handler.postDelayed(this, 3000) // 2 seconds delay
//            }
//        }, 1000)

//        handler.postDelayed({
//            handler.removeCallbacksAndMessages(null)
//        }, 50000)

//        ridecheck()

//        checkRideRequest()
//        drivercheck()
//        checkRideRequest()
//        checkdriverlocation()

        val notification: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val r = RingtoneManager.getRingtone(applicationContext, notification)
        r.play()

        binding.btnAccept.setOnClickListener {
            changeRideStatus()
            r.stop()
            finish()
            startActivity(Intent(this@IncomingRequestActivity, ConfirmPickupActivity::class.java))
        }

        binding.btnReject.setOnClickListener {
            checkRideRequest()
            r.stop()
//            finish()
//            finishAndRemoveTask()
//            checkRideRequest()
//            r.stop()
//            finish()
        }

        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        binding.txtOrigin.text = objectt.pickupAddress
        binding.txtDestination.text = objectt.destinationAddress
        binding.txtestimatedFare.text = " ₦" + objectt.fare

    }

    fun changeRideStatus() {
        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val rideRequest = RideRequestStatusModel(
            true,
            false,
            helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
            Constants.towardsPickup
        )
        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//        myRef.child("RideRequest").child(objectt.passengerID).child("IsRideAccepted").setValue(true)
        myRef.child("RideRequest").child(objectt.passengerID).setValue(rideRequest)
//        Current Ride Working Class
            val database = FirebaseDatabase.getInstance().getReference().child("CurrentRideRequests")
                    .child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString())
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("Firebase Database Found", "Valuse get=" + dataSnapshot.value)
                    val name = dataSnapshot.getValue(ride::class.java)
                    ride.carType = "Car"
                    ride.destinationAddress = name?.destinationAddress.toString()
                    ride.destinationLat = name?.destinationLat.toString()
                    ride.destinationLong = name?.destinationLong.toString()
                    ride.distance = name?.distance.toString()
                    ride.fare = name?.fare.toString()
                    ride.passengerID = name?.passengerID.toString()
                    ride.pickupAddress = name?.pickupAddress.toString()
                    ride.pickupLat = name?.pickupLat.toString()
                    ride.pickupLong = name?.pickupLong.toString()
                    ride.isRideAccepted = false
                    ride.time = name?.time.toString()
                    Log.d(TAG, "Rides  " + ride.toString())
                    FirebaseDatabase.getInstance().getReference().child("CurrentRide")
                        .child(driverId)
                        .setValue(ride)
//                Handler().postDelayed({
//                    FirebaseDatabase.getInstance().getReference().child("CurrentRideRequests").child(driverId).removeValue()
//                                      },5000)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }

            database.addValueEventListener(postListener)

//        val ride = currentride()
//        FirebaseDatabase.getInstance().getReference().child("CurrentRideWorking").child(driverId).setValue(ride)

    }

    @SuppressLint("ResourceType")
    fun checkRideRequest() {
        val rideRequest = RideRequestStatusModel(
            false,
            true,
            helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
            Constants.id
        )

        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//        myRef.child("RideRequest").child(objectt.passengerID).child("driverID").child("")
//        myRef.child("RideRequest").child(objectt.passengerID).child("driverID").setValue("")

        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle("Alert")
            .setMessage("Are you sure you want to reject the ride?")
            .setPositiveButton("YES") { dialog, whichButton ->
                myRef.child("RideRequest").child(objectt.passengerID).child("driverID").child("")
                myRef.child("RideRequest").child(objectt.passengerID).child("driverID").setValue("")

//                checkMyRide()
                finish()
//                finishAndRemoveTask()
//                myRef.child("CurrentRideRequests")
//                    .child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString())
//                    .removeValue()
            }
            .setNegativeButton("NO") { dialog, whichButton ->
//                checkMyRide()
                finish()
                // DO YOUR STAFF
//                     dialog.close()
            }

        dialog.show()

//            val mFragmentManager = supportFragmentManager
//            val mFragmentTransaction = mFragmentManager.beginTransaction()
//            val mFragment = MapsFragment()
//
//            mFragmentTransaction.add(R.id.map,mFragment ).commit()

//            startActivity(
//                    Intent(
//                        this@IncomingRequestActivity,
//                        R.layout.fragment_maps
//                    )
//                )
//            MapsFragment.newInstance("Param1 ", "Param 2")
//                startActivity(
//                    Intent(
//                        this@IncomingRequestActivity,
//                        ProfileUpdateActivity::class.java
//                    )
//                )
                finish()
    }

    fun drivercheck() {
        val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(this@IncomingRequestActivity, "Go Online", Toast.LENGTH_SHORT)
                        .show()
                    binding.btnGoOnline.setText("Go Online")
                } else if (dataSnapshot.exists()) {
                    Toast.makeText(this@IncomingRequestActivity, "Go Offline", Toast.LENGTH_SHORT)
                        .show()
                    binding.btnGoOnline.setText("Go Offline")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        }
        data.addListenerForSingleValueEvent(eventListener)
//        if (data.key.equals(driverid)){
//
//            Toast.makeText(this,"Go Offline",Toast.LENGTH_SHORT).show()
//
//        }else if (!data.key.equals(driverid)){
//
//            Toast.makeText(this,"Go Online",Toast.LENGTH_SHORT).show()
//
//        }
    }

    override fun onBackPressed() {
//        startActivity(
//                    Intent(
//                        this@IncomingRequestActivity,
//                        MainActivity::class.java
//                    )
//                )
//            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun checkMyRide() {
        var objectt = helperClass.getRequestUserDAO(this)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val database = Firebase.database
        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)

        if (driverid != null) {
//            Toast.makeText(this, "No Ride", Toast.LENGTH_LONG).show()
        }
        Log.d(
            "SHAN", "User ID=" + driverid
        )
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
                    Log.d(
                        "SHAN",
                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
                    )
                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
                        startActivity(
                            Intent(
                                applicationContext,
                                ConfirmPickupActivity::class.java
                            )
                        )
                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
                        startActivity(
                            Intent(
                                applicationContext,
                                ConfirmPickupActivity::class.java
                            )
                        )
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)
    }

    fun ridecheck(){

        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        val zonesRef = FirebaseDatabase.getInstance().getReference("RideRequest")
        val zone1Ref = zonesRef.child(objectt.passengerID)
        val zone1NameRef = zone1Ref.child("driverID")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.child("driverID").getValue().toString()
                Log.d(TAG,"Driver ID" +id)

            if (id.isEmpty()) {
                startActivity(
                    Intent(
                        this@IncomingRequestActivity,
                        IncomingRequestActivity::class.java
                    )
                )
//                finish()

            }else if (id.isNotEmpty()){
//                changeRideStatus()
//                startActivity(
//                    Intent(
//                        this@IncomingRequestActivity,
//                        ConfirmPickupActivity::class.java
//                    )
//                )
                startActivity(
                    Intent(
                        this@IncomingRequestActivity,
                        ConfirmPickupActivity::class.java
                    )
                )
//                finish()
                Log.d(TAG,"Driver ID Are Available." +id)
            }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        zone1NameRef.addValueEventListener(postListener)
    }

    fun driverrides() {

        var objectt = helperClass.getRequestUserDAO(this)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val database = Firebase.database
        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)

        if (driverid == "") {
//            Toast.makeText(this, "No Ride", Toast.LENGTH_LONG).show()
            startActivity(Intent(this,IncomingRequestActivity::class.java))
        } else {
            Log.d(
                "SHAN", "User ID=" + driverid
            )
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
                        Log.d(
                            "SHAN",
                            "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
                        )
                        if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
                            startActivity(
                                Intent(
                                    applicationContext,
                                    ConfirmPickupActivity::class.java
                                )
                            )
                        } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(
                                Constants.towardsDestination
                            )
                        ) {
                            startActivity(
                                Intent(
                                    applicationContext,
                                    ConfirmPickupActivity::class.java
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            myRef.addValueEventListener(postListener)
        }
    }

//    fun checkride(){
//
//        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
//            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//
//       val data = FirebaseDatabase.getInstance().getReference()
////           .child("CurrentRide").child(driverId)
//           .child(objectt.passengerID).child("driverID")
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val id = dataSnapshot.value
//               Log.d(TAG,"Driver ID" +dataSnapshot.value)
//
//                if (dataSnapshot.exists()){
//                    Log.d(TAG,"Driver ID Null"+ id)
////                    startActivity(Intent(this@IncomingRequestActivity,IncomingRequestActivity::class.java))
////                    finish()
////                    finish()
////                    finishAndRemoveTask()
//
//                }else{
//                    Log.d(TAG,"Driver ID Available"+ id)
//
////                    checkRide()
////                    changeRideStatus()
////                    finish()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//
//        data.addValueEventListener(postListener)
//    }

//    fun checkRide() {
//        var objectt = helperClass.getRequestUserDAO(this)?.getRequestObject(helperClass.getRoomDAO(applicationContext)?.getUserId().toString().toInt())
//        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        val database = Firebase.database
//        val myRef = database.getReference().child("RideRequest").child(objectt?.passengerID.toString())
//
//        if (driverid != "") {
////            Toast.makeText(this, "No Ride", Toast.LENGTH_LONG).show()
//            Log.d(
//                "SHAN", "User ID=" + driverid
//            )
//            val postListener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if(!dataSnapshot.exists()){
////                      Toast.makeText(this@MainActivity,"No Ride",Toast.LENGTH_LONG).show()
//                    }
//                    if (dataSnapshot.exists()) {
//                        rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                        Log.d(
//                            "SHAN",
//                            "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                        )
//                        if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                            startActivity(
//                                Intent(
//                                    applicationContext,
//                                    ConfirmPickupActivity::class.java
//                                )
//                            )
//                        } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                            startActivity(
//                                Intent(
//                                    applicationContext,
//                                    ConfirmPickupActivity::class.java
//                                )
//                            )
//                        }
//                    }
//                }
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//                }
//            }
//
//            myRef.addValueEventListener(postListener)
//
//        }
////        Log.d(
////            "SHAN", "User ID=" + driverid
////        )
////        val postListener = object : ValueEventListener {
////            override fun onDataChange(dataSnapshot: DataSnapshot) {
////                if (dataSnapshot.exists()) {
////                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
////                    Log.d(
////                        "SHAN",
////                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
////                    )
////                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
////                        startActivity(
////                            Intent(
////                                applicationContext,
////                                ConfirmPickupActivity::class.java
////                            )
////                        )
////                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
////                        startActivity(
////                            Intent(
////                                applicationContext,
////                                ConfirmPickupActivity::class.java
////                            )
////                        )
////                    }
////                }
////            }
////            override fun onCancelled(databaseError: DatabaseError) {
////                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
////            }
////        }
//
//    }

//    fun clearActivity(){
//        Handler().postDelayed({
//            startActivity(Intent(this,MainActivity::class.java))
//            finish()
//        },1000)
//    }

    fun RideCondition(){

        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        var objectt = helperClass.getRequestUserDAO(this@IncomingRequestActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        val data = FirebaseDatabase.getInstance().getReference().child(objectt.passengerID).child("driverID")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.value
               Log.d(TAG,"Driver ID" +id)
                if(id == ""){
                    startActivity(Intent(this@IncomingRequestActivity,IncomingRequestActivity::class.java))

                }else{
//                    changeRideStatus()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        data.addValueEventListener(postListener)
    }
//
//    override fun onResume() {
//        handler.postDelayed(Runnable {
//            handler.postDelayed(runnable!!, delay.toLong())
//            RideCondition()
//        }.also { runnable = it }, delay.toLong())
//        super.onResume()
//    }
//    override fun onPause() {
//        super.onPause()
//        handler.removeCallbacks(runnable!!)
//    }
}