package com.goridesnigeria.appServices

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.goridesrider.data.model.LatLongModel
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.utils.HelperClass

class DriverLocationUpdateService : Service(), LocationListener {

    private var mHandler: Handler? = null
    var id: String? = null
    private var locationManager: LocationManager? = null
    private var provider: String? = null
    var locationUser: Location? = null
    var locationTrackModel: LatLongModel? = null
    val helperClass = HelperClass()
    val RideRequest = RideRequestStatusModel()
    val database = Firebase.database
//    OnlineDrivers
    val geoFire = GeoFire(Firebase.database.getReference("OnlineDrivers"))

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val runnableService: Runnable = object : Runnable {
        override fun run() {
            syncData()
//            mHandler!!.postDelayed(this, 10000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("SHAN", "Inservice")
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)

        checkLocation()

        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager!!.requestLocationUpdates(provider!!, 0, 0f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationUser = location
                    //   Toast.makeText(ServiceUpdateDriverLocation.this, "Locaiton=" + locationUser.getLongitude(), Toast.LENGTH_SHORT).show();
                }
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            })
        }

    }

    fun checkLocation() {

        val criteria = Criteria()
        provider = locationManager!!.getBestProvider(criteria, false)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = locationManager!!.getLastKnownLocation(provider!!)
        locationUser = location
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Create the Handler object

        // Create the Handler object
        // id = intent.getStringExtra("userID")
        Log.d("SHAN", "IN SERVICE=$id")
        mHandler = Handler()
        mHandler!!.post(runnableService)
        return START_STICKY
    }

    @Synchronized
    private fun syncData() {
//        driverOnline()
        getLastKnownLocation()
//        checkdriverlocation()
        Log.d("SHAN", "Every 10 second")
    }
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onLocationChanged(location: Location) {

        Log.d("SHAN", "Change Location=" + location)
    }

    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                    location ->
                if (location != null) {
                    geoFire.setLocation(
                        helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
                        GeoLocation(location!!.latitude, location!!.longitude)
                    )
                    Log.d("SHAN", "Lat long new" + location + helperClass.getRoomDAO(applicationContext)!!.getUserId().toString())

//                    val bundle = Bundle()
//                    bundle.putString("driverId", "driverlocation")
//                    val myIntent = Intent(this, MapsFragment::class.java)
//                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                } else {
                    Log.d("SHAN", "Lat Long new")
                }

            }
    }
    fun driverOfflineLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    geoFire.setLocation(
                        helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
                        GeoLocation(location!!.latitude, location!!.longitude)
                    )
                    Log.d("SHAN", "Lat long new" + location.latitude)
                } else {
                    Log.d("SHAN", "location nulls")
                }
            }
    }

//    fun checkdriverlocation(){
//
//        val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()
//        val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
//        data.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (data in snapshot.children) {
//                    if (data.child(driverid).exists()){
//
//                    }else{
//                        Toast.makeText(applicationContext,"sdfg",Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {

//            }
//
//        })
//
//    }

    fun driverOnline(){

        val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers")
        data.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (data.key.equals(driverid)){
                    Toast.makeText(this@DriverLocationUpdateService,"Driver Are Available",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@DriverLocationUpdateService,"Driver Are Not Available",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })
    }
}