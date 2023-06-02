package com.goridesnigeria.goridesrider

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.LocationCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.goridesrider.Fragments.MapsFragment
import com.goridesnigeria.goridesrider.Fragments.ProfileFragment
import com.goridesnigeria.goridesrider.Fragments.RidesFragment
import com.goridesnigeria.goridesrider.Fragments.TransactionHistoryFragment
import com.goridesnigeria.goridesrider.data.RoomDatabase.UserData.RequestBookingDatabase
import com.goridesnigeria.goridesrider.data.api.ApiService
import com.goridesnigeria.goridesrider.data.model.RequestBookingModel
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.utils.Constants
import com.goridesnigeria.goridesrider.utils.HelperClass
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val TAG = "PermissionDemo"
    val database = Firebase.database
    val myRef = database.getReference().child("CurrentRideRequests")
    var requestBooking = RequestBookingModel()
    val helperClass = HelperClass()
    private val RECORD_REQUEST_CODE = 101
    var rideStatus = RideRequestStatusModel()
    var rideStatusRoute: String = ""
    lateinit var apiService: ApiService
    var driveroffline = "Go Offline"
    var driveronline = "Go Online"
    private var functionHasRun = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.bottomNavigation.inflateMenu(R.menu.drawer_menu)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListner)
        setupPermissions()
        initFleetFragment()
        checkRideRequest()
        drivercheck()
        checkdriver()
        checkMyRide()
    //    CurrentDriverRide()
    //    checkdriverRide()

    //        checkMyRide()
    //        checkMyRide()
    //        NewRide()
    //        RideCurrent()
    //        Current Ride Status
    //        currentRideStatus()
    //        checkMyRide()
    //        checkMyRide()
    //        riderequest()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val bottomNavigationListner =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navHome -> {
                    initFleetFragment()
//                    drivercheck()
//                    checkMyRide()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navRides -> {
//                    checkonlinedriver()
                    replaceFragment(
                        RidesFragment.newInstance("Param1 ", "Param 2"),
                        "Home"
                    )
                    return@OnNavigationItemSelectedListener true
                }
//                R.id.navNoty -> {
//                    return@OnNavigationItemSelectedListener true
//                }
                R.id.navtransation -> {
//                    checkonlinedriver()
                    replaceFragment(
                        TransactionHistoryFragment.newInstance("Param1 ", "Param 2"),
                        "Home"
                    )
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navSattings -> {
//                    checkonlinedriver()
                    replaceFragment(
                        ProfileFragment.newInstance("Param1 ", "Param 2"),
                        "Home"
                    )
                    return@OnNavigationItemSelectedListener true
                }

                /*    R.id.menu_profile -> {
                        binding.imgLogout.setVisibility(View.VISIBLE)
                        replaceFragment(ProfileFragment.newInstance("Param1 ", "Param 2"), "My Profile")
                        return@OnNavigationItemSelectedListener true
                    }*/
            }
            false
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun initFleetFragment() {
        replaceFragment(
            MapsFragment.newInstance("Param1 ", "Param 2"),
            "Home"
        )
    }

    fun replaceFragment(fragment: Fragment?, title: String?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment!!, "")
        fragmentTransaction.commit()
//        fragmentTransaction.commitAllowingStateLoss()
    }

    fun checkRideRequest() {
//        drivercheck()
        myRef.child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d("SHAN", "data=" + dataSnapshot)
                    if (dataSnapshot.getValue() != null) {
                        requestBooking = dataSnapshot.getValue(RequestBookingModel::class.java)!!
                        chekRideStatus(requestBooking.passengerID, requestBooking)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Database", error.toString())
                }
            })
    }
    fun chekRideStatus(passengerID: String, requestBooking: RequestBookingModel) {
//        drivercheck()
//        currentRideStatus()
        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val myRef = database.getReference().child("RideRequest").child(passengerID).child("isRideAccepted")
        val ride = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverid).toString()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                Log.d(TAG,"Current Ride "+ride)
                if(dataSnapshot.exists()) {
                        if (dataSnapshot.value as Boolean) {

                        } else {
                            val obj = RequestBookingDatabase(
                                requestBooking.passengerID,
                                requestBooking.pickupAddress,
                                requestBooking.pickupLat,
                                requestBooking.pickupLong,
                                requestBooking.destinationAddress,
                                requestBooking.destinationLong,
                                requestBooking.destinationLat,
                                requestBooking.firstStopAddress,
                                requestBooking.firstStopLat,
                                requestBooking.firstStopLong,
                                requestBooking.secondStopAddress,
                                requestBooking.secondStopLat,
                                requestBooking.secondStopLong,
                                requestBooking.carType,
                                requestBooking.rideType,
                                requestBooking.fare,
                                requestBooking.distance,
                                requestBooking.time,
                                requestBooking.isRideAccepted,
                                helperClass.getRoomDAO(applicationContext)!!.getUserId()
                            )
                            helperClass.getRequestUserDAO(this@MainActivity)!!.deletAll()
                            helperClass.getRequestUserDAO(this@MainActivity)!!.insert(obj)
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    IncomingRequestActivity::class.java
                                )
                            )
                        }
//                    }
                    } else if (!dataSnapshot.exists()) {
                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        myRef.addValueEventListener(postListener)

    }

    @RequiresApi(Build.VERSION_CODES.N)

    fun checkdriverRide() {

        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        var objectt = helperClass.getRequestUserDAO(this)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        val data = FirebaseDatabase.getInstance().getReference().child("RideRequest")
            .child(objectt.passengerID).child("driverID")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val id = dataSnapshot.value
                Log.d(ContentValues.TAG,"Driver ID" +dataSnapshot.value)

                if (id == ""){
                    Log.d(ContentValues.TAG,"Driver ID Null"+ id)
                    startActivity(Intent(this@MainActivity,MainActivity::class.java))
                }else{
                    Log.d(ContentValues.TAG,"Driver ID Available"+ id)
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
                    }else if (!dataSnapshot.exists()){
                        Log.d(TAG,"Ride"+"No Ride" )
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        data.addValueEventListener(postListener)
    }

//    fun checkMyRide() {
//       val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
////        val myRef = database.getReference().child("RideRequest")
////            .child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString())
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
////                if (dataSnapshot.exists()){
//                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                    if(rideStatus.rideStatus.equals(Constants.rideCompleted)){
//                        val data = FirebaseDatabase.getInstance().getReference("CurrentRideRequests").child(driverid)
//                        data.addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                for (data in dataSnapshot.children) {
//                                    data.ref.removeValue()
//                                }
//                            }
//                            override fun onCancelled(databaseError: DatabaseError) {
//                            }
//                        })
//                    }
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        }
//        myRef.addValueEventListener(postListener)
//    }

        fun getDriverData() {

            val geoFire = GeoFire(Firebase.database.getReference("OnlineDrivers"))
            geoFire.getLocation(
                helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
                object : LocationCallback {
                    override fun onLocationResult(key: String?, location: GeoLocation?) {
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                    }
                })
        }


    fun checkCompleteRide() {
        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("CurrentRideRequest").child(driverId)

//        if ()
    }
    private fun setupPermissions() {

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setMessage("Permission to access the Location is required for this app to Use Taxi.")
                .setTitle("Go Rides Rider")
            builder.setPositiveButton(
                "OK"
            )
            { dialog, id ->
                Log.i(ContentValues.TAG, "Clicked")
                makeRequest()
            }
            val dialog = builder.create()
            dialog.show()
        }
        else{ }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            RECORD_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(ContentValues.TAG, "Permission has been denied by user")
                } else {
                    Log.i(ContentValues.TAG, "Permission has been granted by user")
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        finish()
    }

    fun drivercheck(){

        val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
//                    Toast.makeText(this@MainActivity,"Go Online", Toast.LENGTH_SHORT).show()
                }
                else if (dataSnapshot.exists()){
//                    Toast.makeText(this@MainActivity,"Go Offline",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        }
        data.addListenerForSingleValueEvent(eventListener)

//        if (data.key.equals(driverid)){
//            Toast.makeText(this,"Go Offline",Toast.LENGTH_SHORT).show()
//        }else if (!data.key.equals(driverid)){
//            Toast.makeText(this,"Go Online",Toast.LENGTH_SHORT).show()
//        }
    }

//    fun checkride(){
//
//        if (dataSnapshot.exists()) {
//            rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//            Log.d(
//                "SHAN",
//                "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//            )
//            if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                rideStatusRoute = rideStatus.rideStatus
//                (startActivity(
//                    Intent(
//                        this@MainActivity,
//                        ConfirmPickupActivity::class.java
//                    )
//                ))
//                Log.d(
//                    "SHAN",
//                    "Result" + rideStatusRoute + " " + rideStatus.rideStatus
//                )
//            } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                (startActivity(
//                    Intent(
//                        this@MainActivity,
//                        ConfirmPickupActivity::class.java
//                    )
//                ))
//            }
//        }
//    }

    fun checkMyRide() {
        var objectt = helperClass.getRequestUserDAO(this)?.getRequestObject(helperClass.getRoomDAO(applicationContext)?.getUserId().toString().toInt())
        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val database = Firebase.database
        val myRef = database.getReference().child("RideRequest").child(objectt?.passengerID.toString())

        if (driverid != "") {
//            Toast.makeText(this, "No Ride", Toast.LENGTH_LONG).show()
            Log.d(
                "SHAN", "User ID=" + driverid
            )
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(!dataSnapshot.exists()){
//                      Toast.makeText(this@MainActivity,"No Ride",Toast.LENGTH_LONG).show()
                    }
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
//        Log.d(
//            "SHAN", "User ID=" + driverid
//        )
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                    Log.d(
//                        "SHAN",
//                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                    )
//                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            )
//                        )
//                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            )
//                        )
//                    }
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }

    }

    fun riderequest(){
        var objectt = helperClass.getRequestUserDAO(this)!!
            .getRequestObject(helperClass.getRoomDAO(this)!!.getUserId().toInt())
        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)
        val postListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                if(dataSnapshot.exists()){
                    startActivity(Intent(this@MainActivity,IncomingRequestActivity::class.java))
                    finish()
                }else if (!dataSnapshot.exists()){
                    replaceFragment(
                        RidesFragment.newInstance("Param1 ", "Param 2"),
                        "Home"
                    )
                    return

//                    val mapsfragment = MapsFragment()
//                    val fragment: Fragment? = supportFragmentManager.findFragmentByTag(MapsFragment::class.java.simpleName)
//                    if (fragment !is MapsFragment){
//                        supportFragmentManager.beginTransaction().add(R.id.fragment_container,mapsfragment,MapsFragment::class.java.simpleName)
//                            .commit()
//                    }
//                    initFleetFragment()
//                    val fragmentManager: FragmentManager = supportFragmentManager
//                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.fragment_container,MapsFragment.newInstance("Param1 ", "Param 2"),"Home").commit()
//                    replaceFragment(
//                        MapsFragment.newInstance("Param1 ", "Param 2"),
//                        "Home"
//                    )
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        myRef.addValueEventListener(postListener)
    }

    fun currentRideStatus(){
        var objectt = helperClass.getRequestUserDAO(this)!!
            .getRequestObject(helperClass.getRoomDAO(this)!!.getUserId().toInt())
        val myRef = database.getReference().child("RideRequest")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                if(dataSnapshot.exists()){

                    Toast.makeText(this@MainActivity,"Ride Already Working",Toast.LENGTH_LONG).show()

                }else if (!dataSnapshot.exists()) {

                    Toast.makeText(this@MainActivity,"Ride Not Working",Toast.LENGTH_LONG).show()

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        myRef.addValueEventListener(postListener)
    }

    fun RideCurrent(){

        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val myRef = database.getReference().child("CurrentRideWorking").child(driverid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                if(dataSnapshot.exists()){

//                    Toast.makeText(this@MainActivity,"Ride Already Working",Toast.LENGTH_LONG).show()
//                    checkMyRide()
//                    startActivity(Intent(this@MainActivity,MainActivity::class.java))

                }else if (!dataSnapshot.exists()) {
                    checkRideRequest()
//                    Toast.makeText(this@MainActivity,"Ride Not Working",Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        myRef.addValueEventListener(postListener)
    }

    fun NewRide(){

        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val ride = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                if(dataSnapshot.exists()){

//                    Toast.makeText(this@MainActivity,"Ride Already Working",Toast.LENGTH_LONG).show()
//                    checkMyRide()
                    startActivity(Intent(this@MainActivity,MainActivity::class.java))

                }else if (!dataSnapshot.exists()) {
//                    checkRideRequest()
                    Toast.makeText(this@MainActivity,"Ride Not Working",Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        ride.addValueEventListener(postListener)
    }

    fun checkdriver(){

        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverid)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
            if(dataSnapshot.exists()){
//                Toast.makeText(this@MainActivity,"Driver ID"+driverid,Toast.LENGTH_LONG).show()
                Log.d(TAG,"Current Driver Ride ID = " +driverid)
//                startActivity(Intent(this@MainActivity,ConfirmPickupActivity::class.java))
//                finish()
//                checkMyRide()

            }else{
//                Toast.makeText(this@MainActivity,"Driver ID Not Found"+driverid,Toast.LENGTH_LONG).show()
                Log.d(TAG,"Driver ID Not Found =")
//                startActivity(Intent(this@MainActivity,MainActivity::class.java))
            }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        data.addValueEventListener(postListener)
    }

    fun checkdriverrRide(){

        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference().child("CurrentRide").child(driverid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
//                Toast.makeText(this@MainActivity,"Driver ID"+driverid,Toast.LENGTH_LONG).show()
                    Log.d(TAG,"Current Driver Ride ID = " +driverid)
//                startActivity(Intent(this@MainActivity,ConfirmPickupActivity::class.java))
//                finish()
//                checkMyRide()

                }else{
//                Toast.makeText(this@MainActivity,"Driver ID Not Found"+driverid,Toast.LENGTH_LONG).show()
                    Log.d(TAG,"Driver ID Not Found =")
//                startActivity(Intent(this@MainActivity,MainActivity::class.java))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        data.addValueEventListener(postListener)
    }
//    fun CurrentDriverRide(){

//        val driverId = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        var objectt = helperClass.getRequestUserDAO(this)!!
//            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//
//        val data = FirebaseDatabase.getInstance().getReference().child("RideRequest")
//            .child(objectt.passengerID).child("driverID")
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val id = dataSnapshot.value
//                Log.d(ContentValues.TAG,"Driver ID" +dataSnapshot.value)
//
//                if (id == ""){
//                    Log.d(ContentValues.TAG,"Driver ID Null"+ id)
//                }else{
//                    Log.d(ContentValues.TAG,"Driver ID Available"+ id)
//
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//
//        data.addValueEventListener(postListener)
//
//    }

//    fun CurrentDriverRide() {
//
//        val driverID = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        var objectt = helperClass.getRequestUserDAO(this)!!
//            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//        val userObject = getSharedPreferences("UserObject", Context.MODE_PRIVATE)
//        val database = Firebase.database
//        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID).child("driverID")
//
//        if (driverID == null) {
//            Toast.makeText(this, "No Ride", Toast.LENGTH_LONG).show()
//        }else{
//        Log.d(
//            "Hamza Zulfiqar", "User ID=" + userObject.getString(
//                Constants.id, ""
//            )!!
//        )
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val data = dataSnapshot.value
//                Log.d(TAG,"Current Ride Details"+dataSnapshot.value)
//                if (dataSnapshot.exists()) {
//                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                    Log.d(
//                        "Hamza Zulfiqar",
//                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                    )
//                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            )
////                                .putExtra(Constants.driverID, rideStatus.driverID)
////                                .putExtra(Constants.rideMode, rideStatus.rideStatus)
//                        )
//                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            )
////                                .putExtra(Constants.driverID, rideStatus.driverID)
////                                .putExtra(Constants.rideMode, rideStatus.rideStatus)
//                        )
//                    }
////                    else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(
////                            Constants.rideCompleted
////                        )
////                    ) {
////                        showDialog("title")
////                        startActivity(
////                            Intent(
////                                applicationContext,
////                                DriverAcceptRequestActivity::class.java
////                            ).putExtra(Constants.driverID, rideStatus.driverID)
////                                .putExtra(Constants.rideMode, rideStatus.rideStatus)
////                        )
////                    }
//                }else if (!dataSnapshot.exists()){
//                    Toast.makeText(this@MainActivity, "No Ride", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        myRef.addValueEventListener(postListener)
//    }
//        }

//    fun CurrentDriverRide(){
//
//        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        val objject = helperClass.getRequestUserDAO(this)!!
//            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
//
//        val data = FirebaseDatabase.getInstance().getReference().child("RideRequest")
//            .child(objject.passengerID).child("driverID")
//
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val id = dataSnapshot.value
//                Log.d(ContentValues.TAG,"Driver ID" +dataSnapshot.value)
//
//                if (id == ""){
//                    Log.d(ContentValues.TAG,"Driver ID Null"+ id)
//                    Toast.makeText(this@MainActivity,"No Current Ride",Toast.LENGTH_SHORT).show()
//
//                }else if (id != ""){
//
//                    Log.d(ContentValues.TAG,"Driver ID Available"+ id)
//                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                    Log.d(
//                        "SHAN",
//                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                    )
//                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            ).putExtra(Constants.driverID, rideStatus.driverID)
//                                .putExtra(Constants.rideMode, rideStatus.rideStatus)
//                        )
//                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                        startActivity(
//                            Intent(
//                                applicationContext,
//                                ConfirmPickupActivity::class.java
//                            ).putExtra(Constants.driverID, rideStatus.driverID)
//                                .putExtra(Constants.rideMode, rideStatus.rideStatus)
//                        )
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//
//        data.addValueEventListener(postListener)
//    }
}

//                if (dataSnapshot.exists()) {
//                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
//                    Log.d(
//                        "SHAN",
//                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
//                    )
//                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
//                        rideStatusRoute = rideStatus.rideStatus
//                        (startActivity(
//                            Intent(
//                                this@MainActivity,
//                                ConfirmPickupActivity::class.java
//                            )
//                        ))
//                        Log.d(
//                            "SHAN",
//                            "Result" + rideStatusRoute + " " + rideStatus.rideStatus
//                        )
//                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
//                        (startActivity(
//                            Intent(
//                                this@MainActivity,
//                                ConfirmPickupActivity::class.java
//                            )
//                        ))
//                    }
//                }