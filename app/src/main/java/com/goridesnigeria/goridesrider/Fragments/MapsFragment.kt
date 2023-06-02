package com.goridesnigeria.goridesrider.Fragments

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.firebase.geofire.GeoFire
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.appServices.DriverLocationUpdateService
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.data.model.LatLongModel
import com.goridesnigeria.goridesrider.data.model.RequestBookingModel
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.model.GoRideAppplication
import com.goridesnigeria.goridesrider.utils.Constants
import com.goridesnigeria.goridesrider.utils.HelperClass

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MapsFragment : Fragment(), LocationListener {
    private var param1: String? = null
    private var param2: String? = null

    val helperClass = HelperClass()
    var location: Location? = null
    lateinit var map: GoogleMap
    lateinit var FusedLocationClient: FusedLocationProviderClient
    lateinit var binding: com.goridesnigeria.goridesrider.databinding.FragmentMapsBinding
    val database = Firebase.database
    var requestModel = RequestBookingModel()
    var rideStatus = RideRequestStatusModel()
    var rideStatusRoute: String = ""
    //    OnlineDrivers

    val totalAmount = GeoFire(Firebase.database.getReference("RideHistory"))
    lateinit var databasereference: DatabaseReference

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        this.map = googleMap
        getLocation()
        drivercheck()
        checkMyRide()
//        driverPayment()
//        checkdriverlocation()
//        updateDriverLocation()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { binding = DataBindingUtil.inflate( inflater, com.goridesnigeria.goridesrider.R.layout.fragment_maps, container, false)

//          binding.btnGoOnline.setOnClickListener {
//              startActivity(
//                  Intent(
//                      context,
//                      IncomingRequestActivity::class.java
//                  )
//              )
//          }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drivercheck()
        checkMyRide()
//        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
//        var objectt = helperClass.getRequestUserDAO(GoRideAppplication.instance)!!
//            .getRequestObject(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toInt())
//        requireActivity()!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        var objectt = helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toInt()
        databasereference = FirebaseDatabase.getInstance().getReference("RideHistoryDriver").child(helperClass.getRoomDAO(requireContext())!!.getUserId().toString())

        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (data in dataSnapshot.children){
                    sum += data.child("fare").getValue().toString().toInt()
                    binding.total.text = " ₦" + sum.toString()
                    Log.d(TAG,"Total Fare Value" +sum * 25 / 100)
                }

                }
            override fun onCancelled(databaseError: DatabaseError) {
            }

        })

        requireActivity().stopService(
            Intent(
                requireContext(),
                DriverLocationUpdateService::class.java
            )
        )
        binding.btnGoOnline.text == "Go Online"

//        val offline = "Go Offline"
//        val online = "Go Online"

//        if (binding.btnGoOnline.text == offline){

//        }else{
//            requireActivity().startService(
//                Intent(
//                    requireContext(),
//                    DriverLocationUpdateService::class.java
//                )
//            )
//        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

//        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()

//        Toast.makeText(
//                 context,
//                 "USerObject=" + helperClass.getRoomDAO(requireActivity())!!.getUserId(),
//                 Toast.LENGTH_SHORT
//             ).show()


        binding.btnGoOnline.setOnClickListener {

            if (binding.btnGoOnline.text == "Go Online") {
                requireActivity().startService(
                    Intent(
                        requireContext(),
                        DriverLocationUpdateService::class.java
                    )
                )
                binding.btnGoOnline.setText("Go Offline")
            } else if (binding.btnGoOnline.text == "Go Offline") {
                val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
                val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
                data.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {
                            data.ref.removeValue()
                            binding.btnGoOnline.setText("Go Online")
                            requireActivity().stopService(
                                Intent(
                                    requireContext(),
                                    DriverLocationUpdateService::class.java
                                )
                            )
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }
        }


////            if (binding.btnGoOnline.text == "Go Offline"){
////                requireActivity().stopService(
////                    Intent(
////                        requireContext(),
////                        DriverLocationUpdateService::class.java
////                    )
////                )
////                binding.btnGoOnline.setText("Go Online")
////            }
//
//
////            Toast.makeText(
////                context,
////                "USerObject=" + driverid,
////                Toast.LENGTH_SHORT
////            ).show()
//
////            Start Driver Condition
////            val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
////            val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
////            data.addListenerForSingleValueEvent(object : ValueEventListener {
////                override fun onDataChange(dataSnapshot: DataSnapshot) {
////                    for (data in dataSnapshot.children) {
////                        data.ref.removeValue()
////                        binding.btnGoOnline.setText("Go Online")
////                        binding.btnGoOnline.setBackgroundColor(Color.RED)
////                        requireActivity().stopService(
////                            Intent(
////                                requireContext(),
////                                DriverLocationUpdateService::class.java
////                            )
////                        )
////                    }
////                }
////                override fun onCancelled(databaseError: DatabaseError) {
////                }
////
////            })
////          End Driver Condition
////            if (binding.btnGoOnline.text == offline){
////                requireActivity().stopService(
////                Intent(
////                    requireContext(),
////                    DriverLocationUpdateService::class.java
////                )
////            )
////            } else if (binding.btnGoOnline.text == online){
//
////            }
////            val rootRef = FirebaseDatabase.getInstance().getReference("OnlineDrivers")
////            val userNameRef = rootRef.child(driverid)
////            val eventListener: ValueEventListener = object : ValueEventListener {
////                override fun onDataChange(dataSnapshot: DataSnapshot) {
////                    if (!dataSnapshot.exists()) {
////                        binding.btnGoOnline.setText("Go Online")
////                        binding.btnGoOnline.setBackgroundColor(Color.GREEN)
////                    }
////                    else {
////                        binding.btnGoOnline.setText("Go Offline")
////                        binding.btnGoOnline.setBackgroundColor(Color.RED)
////                    }
////                }
////                override fun onCancelled(databaseError: DatabaseError) {
////                    Log.d(TAG, databaseError.message)
////                    //Don't ignore errors!
////                }
////            }
////            userNameRef.addListenerForSingleValueEvent(eventListener)
//        }
    }

    fun changeDriverStatus(check:Boolean){

        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
        val rootRef = FirebaseDatabase.getInstance().getReference("OnlineDrivers")
        val userNameRef = rootRef.child(driverid)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    binding.btnGoOnline.setText("Go Online")
                    binding.btnGoOnline.setBackgroundColor(Color.GREEN)
                }else{
                    binding.btnGoOnline.setText("Go Offline")
                    binding.btnGoOnline.setBackgroundColor(Color.RED)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message) //Don't ignore errors!
            }
        }
        userNameRef.addListenerForSingleValueEvent(eventListener)
//        val firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("OnlineDrivers").child(driverid)
//        firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (appleSnapshot in dataSnapshot.children) {
//                    appleSnapshot.ref.removeValue()
//                    binding.btnGoOnline.setText("Go Offline")
//                    binding.btnGoOnline.setBackgroundColor(Color.GREEN)
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException())
//            }
//        })
    }

        fun getLocation() {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            FusedLocationProviderClient(requireContext()).lastLocation
                .addOnCompleteListener { task: Task<Location?> ->
                    location = task.result
                    if (location != null && map != null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location!!.latitude, location!!.longitude), 15f
                            )
                        )
                        map.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    location!!.latitude,
                                    location!!.longitude
                                )
                            )
                                .title("Current Locaiton").icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.car_thirty)
                                )
                        )
                        map.isMyLocationEnabled = true
                        // Handler().postDelayed({ updateDriverLocation() }, 2000)
                    }
                }
        }

        fun updateDriverLocation() {
            Log.d("SHAN", "updateDriverLocation")

            val myLatLong = LatLongModel(
                helperClass.getRoomDAO(requireContext())!!.getUserId().toString(),
                location!!.latitude.toString(), location!!.longitude.toString(), true
            )
            val database = Firebase.database
            val myRef = database.getReference().child("LiveDrivers")
            myRef.child(helperClass.getRoomDAO(requireContext())!!.getUserId().toString())
                .setValue(myLatLong)

        }

    override fun onLocationChanged(p0: Location) {
        map.clear()
        map.addMarker(
            MarkerOptions().position(LatLng(p0!!.latitude, p0!!.longitude)).title("My Locaiton")
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.car_thirty)
                )
        )
    }

    fun drivercheck(){

        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
//                    Toast.makeText(requireContext(),"Go Online", Toast.LENGTH_SHORT).show()
                    binding.btnGoOnline.setText("Go Online")
                }
                else if (dataSnapshot.exists()){
//                    Toast.makeText(requireContext(),"Go Offline",Toast.LENGTH_SHORT).show()
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

//    fun driverPayment(){
//        databasereference = FirebaseDatabase.getInstance().getReference("DriverPayment")
//            .child(helperClass.getRoomDAO(context)!!.getUserId().toString())
//        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var sum = 0
//                for (data in dataSnapshot.children){
//                    sum += data.child("fare").getValue().toString().toInt()
////                    binding.driverbalance.text = " ₦ " + sum
//                    Log.d(TAG,"Total Fare Value" +sum )
//                    if (sum >= 0 && sum <= 100){
////                        Toast.makeText(requireContext(),"Online",Toast.LENGTH_SHORT).show()
//                    }else{
////                        Toast.makeText(requireContext(),"Please Clear Balance First",Toast.LENGTH_SHORT).show()
//                    }
//                }
////                if (!dataSnapshot.exists()){
////                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//            }
//
//        })
//    }

    fun checkCurrentRideRequest(){
        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
        val data = FirebaseDatabase.getInstance().getReference("CurrentRideRequest").child(driverid)
    }

    fun checkMyRide() {
        val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
        val database = Firebase.database
        val myRef = database.getReference().child("RideRequest").child(
            driverid
        )
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
                                requireContext(),
                                DriverLocationUpdateService::class.java
                            )
                        )
                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination)) {
                        startActivity(
                            Intent(
                                requireContext(),
                                DriverLocationUpdateService::class.java
                            )
                        )
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)
    }

    fun CurrentRideRequest(){


    }
}