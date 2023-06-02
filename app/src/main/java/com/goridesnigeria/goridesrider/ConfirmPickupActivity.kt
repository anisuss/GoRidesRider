package com.goridesnigeria.goridesrider

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.goridesnigeria.goridesrider.data.model.RideCompleteModel
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.databinding.ActivityConfirmPickupBinding
import com.goridesnigeria.goridesrider.model.GoRideAppplication
import com.goridesnigeria.goridesrider.model.TotalBalance
import com.goridesnigeria.goridesrider.model.wallet
import com.goridesnigeria.goridesrider.utils.Constants
import com.goridesnigeria.goridesrider.utils.HelperClass
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class ConfirmPickupActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityConfirmPickupBinding

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest
    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    var isFirstCall: Boolean = false
    private lateinit var mMap: GoogleMap
    var location: Location? = null
    var urlDirections: String = ""
    val helperClass = HelperClass()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mHandler: Handler? = null
    var rideRequestStatus = RideRequestStatusModel(true,false,"0","pending")
    var database = Firebase.database
    var myRef = database.getReference()
    var timeDesti: String = ""
    var distanceDesti: String = ""
    var rideStatus = RideRequestStatusModel()
    var rideStatusRoute: String = ""
    lateinit var databasereference: DatabaseReference

    public var sum = 0
    public var total = 0
    public var sub = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        binding = DataBindingUtil.setContentView(
            this@ConfirmPickupActivity,
            R.layout.activity_confirm_pickup
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext!!)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mHandler = Handler()
        mHandler!!.post(runnableService)

//        changeRideStatus()
        chekRideStatus()
//        changeRideStatus()
        checkMyRide()
        binding.btnNext.setOnClickListener {
            changeRideStatus()
//            checkMyRide()
            var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
                .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
            /*        Log.d(
                        "DISTANCE",
                        "Diff=" + getDistance(
                            location!!.latitude,
                            location!!.longitude,
                            objectt.pickupLat.toDouble(),
                            objectt.pickupLong.toDouble()
                        )
                    )*/

        }

//        binding.call.setOnClickListener({
//            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "03311071339"))
//            startActivity(intent)
//        })
        binding.message.setOnClickListener({

            var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
                .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

            val prefs: SharedPreferences = getSharedPreferences("UserObject", Context.MODE_PRIVATE)
            var editor = prefs.edit()
            editor.putString("driverID",objectt.passengerID)
            editor.commit()
            editor.apply()
            startActivity(Intent(this, MessageActivity::class.java))
            finish()

//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "03311071339"))
//            startActivity(intent)

        })
        getLocationUpdates()
    }

    private fun getLocationUpdates() {

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@ConfirmPickupActivity!!)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                if (locationResult.locations.isNotEmpty()) {

                    mMap.clear()
                    drawRount(
                        locationResult.lastLocation!!.latitude,
                        locationResult.lastLocation!!.longitude
                    )

                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                locationResult.lastLocation!!.latitude,
                                locationResult.lastLocation!!.longitude
                            )
                        )
                            .title("Current Locaiton").icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.car_thirty)
                            )
                    )

                }
            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        setValuesInField()
        getLocation()

    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@ConfirmPickupActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@ConfirmPickupActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        FusedLocationProviderClient(this@ConfirmPickupActivity).lastLocation
            .addOnCompleteListener { task: Task<Location?> ->
                location = task.result
                if (location != null && mMap != null) {
                    drawRount(location!!.latitude, location!!.longitude)
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location!!.latitude, location!!.longitude), 15f
                        )
                    )
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(location!!.latitude, location!!.longitude))
                            .title("Current Locaiton").icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.car_thirty)
                            )
                    )

                    mMap.isMyLocationEnabled = true
                    // Handler().postDelayed({ updateDriverLocation() }, 2000)
                }
            }
    }

    fun setValuesInField() {
        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        binding.txtOrigin.text = objectt.pickupAddress
        binding.txtDestination.text = objectt.destinationAddress
        binding.txtFare.text = "Total Fare: " + " ₦  "  + objectt.fare

        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    objectt.pickupLat.toDouble(),
                    objectt.pickupLong.toDouble()
                )
            )
                .title("Current Locaiton").icon(
                    bitmapDescriptorFromVector(applicationContext, R.drawable.pin_droop)
                )
        )
    }

    private val runnableService: Runnable = object : Runnable {
        override fun run() {
            getLastKnownLocation()
            mHandler!!.postDelayed(this, 50000)
        }
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
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("SHAN", "Lat long new" + location.latitude)
                } else {
                    Log.d("SHAN", "location nulls")
                }
            }
    }
    fun drawRount(driverLat: Double, driverLong: Double) {
        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        if (rideRequestStatus.rideStatus.isEmpty() || rideRequestStatus.rideStatus.equals(Constants.towardsPickup)) {
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        objectt.pickupLat.toDouble(),
                        objectt.pickupLong.toDouble()
                    )
                )
                    .title("Pickup Point:" + objectt.pickupAddress).icon(
                        bitmapDescriptorFromVector(applicationContext, R.drawable.pin_droop)
                    )
            )
        }
        if (rideRequestStatus.rideStatus.equals(Constants.towardsDestination)) {

            binding.txtFare.visibility = View.VISIBLE

            urlDirections =
                "https://maps.googleapis.com/maps/api/directions/json?origin=" + driverLat + "," + driverLong + "&destination=" + objectt.destinationLat.toDouble() + "," + objectt.destinationLong.toDouble() + "&key=AIzaSyALIkGkUzfGHj8eIyXUmjnrIui2r_P3r-o"
        } else {
            urlDirections =
                "https://maps.googleapis.com/maps/api/directions/json?origin=" + driverLat + "," + driverLong + "&destination=" + objectt.pickupLat.toDouble() + "," + objectt.pickupLong.toDouble() + "&key=AIzaSyALIkGkUzfGHj8eIyXUmjnrIui2r_P3r-o"

        }

        val path: MutableList<List<LatLng>> = ArrayList()
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val distance = legs.getJSONObject(0).getJSONObject("distance").getString("text")
                val time = legs.getJSONObject(0).getJSONObject("duration").getString("text")

                if (rideRequestStatus.rideStatus.equals(Constants.towardsDestination)) {
                    Log.d("SHAN", "In Draw route=" + rideRequestStatus.rideStatus)
                    if (!isFirstCall) {
                        Log.d("SHAN", "In if" + distance)
                        distanceDesti = distance
                        timeDesti = time
                        isFirstCall = true
                    }
                }

                binding.txtTime.text = time
                binding.txtDistance.text = "(Distance " + distance + ")"
                val steps = legs.getJSONObject(0).getJSONArray("steps")
                for (i in 0 until steps.length()) {
                    val points =
                        steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                    path.add(PolyUtil.decode(points))
                }
                for (i in 0 until path.size) {
                    this.mMap!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.GREEN))
                }
            }, Response.ErrorListener { _ ->
            }) {}


        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        myExecutor.execute {
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(directionsRequest)
        }

        myHandler.post {
            // Do something in UI (front-end process)
        }


    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun changeRideStatus() {
        mMap.clear()
        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
        Log.d("SHAN", "Ride status=" + rideRequestStatus.rideStatus)
        if (rideRequestStatus.rideStatus.equals(Constants.towardsPickup)) {
            rideRequestStatus.rideStatus = Constants.towardsDestination
            binding.btnNext.text = "Click to complete ride"
            binding.txtTitle.text = "Destination"
            binding.txtOrigin.text = binding.txtDestination.text.toString()
            binding.message.visibility = View.GONE
            getLocation()

//            getPassengerTotalBalance()
//            getPassengerRideBalance()
//            AmountDeduction(sub,sum,total)

            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        objectt.destinationLat.toDouble(),
                        objectt.destinationLong.toDouble()
                    )
                )
                    .title("Destination:" + objectt.destinationAddress).icon(
                        bitmapDescriptorFromVector(applicationContext, R.drawable.pin_droop)
                    )
            )
            drawRount(location!!.latitude, location!!.longitude)

        } else if (rideRequestStatus.rideStatus.equals(Constants.towardsDestination)) {
            showDialog("ritle")
            rideRequestStatus.rideStatus = Constants.rideCompleted
            binding.btnNext.text = "Rate passenger"

        }


        myRef.child("RideRequest").child(objectt.passengerID).setValue(rideRequestStatus)

    }

    fun chekRideStatus() {

        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                rideRequestStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
                Log.d("SHAN", "Valuse get=" + rideRequestStatus.rideStatus)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("SHAN", "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(postListener)
    }

    fun checkMyRide() {
        val rideRequest = RideRequestStatusModel(
            true,
            false,
            helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
            Constants.id
        )
//        var objectt = (helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
        var objectt = helperClass.getRequestUserDAO(GoRideAppplication.instance)!!
            .getRequestObject(helperClass.getRoomDAO(GoRideAppplication.instance)!!.getUserId().toInt())
        val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SHAN", "Valuse=" + dataSnapshot.value)
                if (dataSnapshot.exists()) {
                    rideStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
                    Log.d(
                        "SHAN",
                        "Valuse check ride=" + rideStatus.rideStatus + "  " + rideStatus.driverID
                    )
                    if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsPickup)) {
                        rideStatusRoute = rideStatus.rideStatus

//                        Toast.makeText(
//                            this@MainActivity,
//                            "Current towards Pickup",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        (startActivity(Intent(this@ConfirmPickupActivity, ConfirmPickupActivity::class.java)))
                        Log.d(
                            "SHAN",
                            "Result" + rideStatusRoute + " " + rideStatus.rideStatus
                        )
                    } else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.towardsDestination))
                    {
                        binding.btnNext.text = "Click to complete ride"
                        binding.message.visibility = View.GONE
//                        (startActivity(Intent(this@ConfirmPickupActivity, ConfirmPickupActivity::class.java)))
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Current towards Destination",
//                            Toast.LENGTH_LONG
//                        ).show()
                    }
//                    else if (rideStatus.IsRideAccepted && rideStatus.rideStatus.equals(Constants.rideCompleted)) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Ride Complete",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        (startActivity(Intent(this@MainActivity, ConfirmPickupActivity::class.java)))
//                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        myRef.addValueEventListener(postListener)

        fun getDriverData() {

            val geoFire = GeoFire(Firebase.database.getReference("OnlineDrivers"))
            geoFire.getLocation(
                helperClass.getRoomDAO(applicationContext)!!.getUserId().toString(),
                object : com.firebase.geofire.LocationCallback {
                    override fun onLocationResult(key: String?, location: GeoLocation?) {
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                    }
                })
//        binding.progressView.visibility = View.VISIBLE
//        helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()!!
//            .enqueue(object : Callback<DriverModel> {
//            override fun onResponse(call: Call<DriverModel>?, response: Response<DriverModel>?) {
//                runOnUiThread { binding.progressView.visibility = View.GONE }
//                if (response?.body() != null) {
//                    runOnUiThread {
//                        getDriverCurrentLocation()
//                        setValuesInfield(response.body()!!)
//                    }
//                }
//            }
//            override fun onFailure(call: Call<DriverModel>?, t: Throwable?) {
//                runOnUiThread { binding.progressView.visibility = View.GONE }
//            }
//        }
        }
    }
    private fun getDistance(
        startLat: Double,
        startLang: Double,
        endLat: Double,
        endLang: Double
    ): Float {
        val locStart = Location("")
        locStart.latitude = startLat
        locStart.longitude = startLang
        val locEnd = Location("")
        locEnd.latitude = endLat
        locEnd.longitude = endLang
        return locStart.distanceTo(locEnd)
    }

    private fun showDialog(title: String) {

        val dialog = Dialog(this@ConfirmPickupActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_rating)
        //val body = dialog.findViewById(R.id.body) as TextView
        // body.text = title
        val yesBtn = dialog.findViewById(R.id.rank_dialog_button) as Button
        val dialog_ratingbar = dialog.findViewById(R.id.dialog_ratingbar) as RatingBar
        //val noBtn = dialog.findViewById(R.id.noBtn) as TextView
        yesBtn.setOnClickListener {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val currentDateandTime: String = sdf.format(Date())
            var rideCompleteMode = RideCompleteModel()

            var walletAmount = wallet()

            var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
                .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
            rideCompleteMode.pickupAddress = objectt.pickupAddress
            rideCompleteMode.destinationAddress = objectt.destinationAddress
            rideCompleteMode.firstStopAddress = objectt.firstStopAddress
            rideCompleteMode.secondStopAddress = objectt.secondStopAddress
            rideCompleteMode.rating = dialog_ratingbar.rating.toString()
            rideCompleteMode.dateAndTime = currentDateandTime
            rideCompleteMode.passengerID = objectt.passengerID
            rideCompleteMode.driverID =
                helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
            rideCompleteMode.distance = distanceDesti
            rideCompleteMode.time = timeDesti
            rideCompleteMode.fare = objectt.fare
            rideCompleteMode.carType = "Mini"
            rideCompleteMode.rideType = "Single Trip"

            var passengerPayment = objectt.fare

            val pd = ProgressDialog(this)
            pd.setMessage("loading.....")
            pd.show()
            pd.setCanceledOnTouchOutside(false)
            pd.setCancelable(false)

            Log.d(TAG,"Customer Payment"+objectt.fare)
            Log.d(TAG,"Passenger ID"+rideCompleteMode.passengerID)

            Log.d(TAG,"User ID"+ rideCompleteMode.driverID)
            Log.d(TAG,"Deduction Amount"+ rideCompleteMode.fare)

//            var driverpayment =  objectt.fare.toInt() * 25 / 100
//            walletAmount.fare = driverpayment.toString()
//            myRef.child("DriverPayment").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).push().setValue(walletAmount)

            databasereference = FirebaseDatabase.getInstance().getReference("PassengerTotalBalance")
                .child(rideCompleteMode.passengerID)

            databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (data in dataSnapshot.children) {
                        var sum = 0
                        sum += data.child("total").getValue().toString().toDouble().toInt()

                        pd.dismiss()

                        Log.d(TAG, "Total Passenger Balance" + sum)
                        Log.d(TAG,"User ID"+ rideCompleteMode.passengerID)
                        Log.d(TAG,"Deduction Amount" + rideCompleteMode.fare)

//                        Condition Payment Deduction
//                        Last Editing is related to wallet payment deduction.

                        if (objectt.fare < data.child("total").getValue().toString().toDouble().toInt()
                                .toString()){

                            val deduction = sum - rideCompleteMode.fare.toInt()
                            Log.d(TAG,"Payment Received With Passenger" + deduction)
                            data.ref.removeValue()

                                var total = TotalBalance()
                                total.total = deduction.toString()
                                databasereference = FirebaseDatabase.getInstance().getReference("PassengerTotalBalance")
                                    .child(rideCompleteMode.passengerID)

                                myRef.child("PassengerTotalBalance").child(rideCompleteMode.passengerID).push().setValue(total)

                                var driverpayment =  objectt.fare.toInt() * 25 / 100
                                walletAmount.fare = driverpayment.toString()
                                myRef.child("DriverPayment").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).push().setValue(walletAmount)

                                Toast.makeText(this@ConfirmPickupActivity,"Payment Received Successfully",Toast.LENGTH_SHORT).show()

                        }else{

                            Toast.makeText(this@ConfirmPickupActivity,"Please Received Cash And Pay Company",Toast.LENGTH_SHORT).show()
                            var driverpayment = objectt.fare.toInt()  + objectt.fare.toInt() * 25/100
                            walletAmount.fare = driverpayment.toString()
                            myRef.child("DriverPayment").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).push().setValue(walletAmount)

                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }

            })

            walletAmount.fare = passengerPayment.toString()
            myRef.child("RideHistoryCustomer").child(objectt.passengerID).push().setValue(rideCompleteMode)
            myRef.child("PassengerPayment").child(objectt.passengerID).push().setValue(walletAmount)
            myRef.child("CurrentRide").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).removeValue()
//            myRef.child("CurrentRideWorking").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).removeValue()
            myRef.child("Chats").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).removeValue()
            myRef.child("Chats").child(objectt.passengerID).removeValue()
//            myRef.child("Chats").child(objectt.passengerID).removeValue()
//            myRef.child("RideRequest").child(objectt.passengerID).removeValue()

      //    myRef.child("RideHistory").child(helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()).push().setValue(rideCompleteMode)
            dialog.dismiss()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
            finish()
//            AmountDeduction()
        }

        dialog.show()

    }

    fun getPassengerTotalBalance() {

        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        lateinit var databasereference: DatabaseReference
        databasereference = FirebaseDatabase.getInstance().getReference("PassengerTotalBalance")
            .child(objectt.passengerID)
        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    var sum = 0
                    this@ConfirmPickupActivity.sum += data.child("total").getValue().toString().toDouble().toInt()
                    Log.d(ContentValues.TAG, "Total Passenger Balance" +sum)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun getPassengerRideBalance(){

        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        lateinit var databasereference: DatabaseReference

        databasereference = FirebaseDatabase.getInstance().getReference("PassengerPayment")
            .child(objectt.passengerID)

        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {

                    val ride = data.child("fare").getValue().toString()
                    Log.d(ContentValues.TAG, "Total Passenger Ride Balance" +ride)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })

    }

    fun AmountDeduction(){

//        this.sub = sum - total
//        Log.d(TAG,"Payment Collection Passenger"+sub)

        lateinit var databasereference: DatabaseReference

        var objectt = helperClass.getRequestUserDAO(this@ConfirmPickupActivity)!!
            .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())

        databasereference = FirebaseDatabase.getInstance().getReference("PassengerTotalBalance")
            .child(objectt.passengerID)

        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (data in dataSnapshot.children) {
                    sum += data.child("total").getValue().toString().toDouble().toInt()
                    Log.d(TAG, "Total Passenger Balance" +sum)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })

        databasereference = FirebaseDatabase.getInstance().getReference("PassengerPayment")
            .child(objectt.passengerID)

        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val ride = data.child("fare").getValue().toString()
                    Log.d(TAG, "Total Passenger Ride Balance" +ride)
                    Toast.makeText(this@ConfirmPickupActivity," Ride Balance "+ride,Toast.LENGTH_LONG).show()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })
    }

    override fun onBackPressed() {

        Toast.makeText(this,"Please Current Ride Complete",Toast.LENGTH_LONG).show()

    }
}