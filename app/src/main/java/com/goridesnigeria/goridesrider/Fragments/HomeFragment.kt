package com.goridesnigeria.goridesrider.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.goridesrider.databinding.FragmentHomeBinding
import com.goridesnigeria.goridesrider.utils.HelperClass


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var mMap: GoogleMap? = null
internal lateinit var mLastLocation: Location
internal lateinit var mLocationResult: LocationRequest
internal lateinit var mLocationCallback: LocationCallback
internal var mCurrLocationMarker: Marker? = null
internal var mGoogleApiClient: GoogleApiClient? = null
internal lateinit var mLocationRequest: LocationRequest
lateinit var FusedLocationClient: FusedLocationProviderClient

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null
    var map: GoogleMap? = null

    lateinit var binding: FragmentHomeBinding

    //     Firebase Database
    val database = Firebase.database
    //    OnlineDrivers
    val geoFire = GeoFire(Firebase.database.getReference("OnlineDrivers"))
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val helperClass = HelperClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                com.goridesnigeria.goridesrider.R.layout.fragment_home,
                container,
                false
            )

//        binding.btnGoOnline.setOnClickListener{
//
//        }

       binding.btnGoOnline.setOnClickListener {

           if (ActivityCompat.checkSelfPermission(
                   requireContext(),
                   Manifest.permission.ACCESS_FINE_LOCATION
               ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                   requireContext(),
                   Manifest.permission.ACCESS_COARSE_LOCATION
               ) != PackageManager.PERMISSION_GRANTED
           ) {
               return@setOnClickListener
           }
           FusedLocationClient!!.lastLocation
               .addOnSuccessListener { location ->
                   if (location != null) {
                       geoFire.setLocation(
                           helperClass.getRoomDAO(requireContext())!!.getUserId().toString(),
                           GeoLocation(location!!.latitude, location!!.longitude)
                       )
                       Log.d("SHAN", "Lat long new" + location.latitude + id)
                       binding.btnGoOnline.setText("Go Offline")
                   } else {
                       Log.d("SHAN", "Lat Long new")
                   }
               }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(37.4233438, -122.0728817),
                10f
            )
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission to access location grant
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap!!.isMyLocationEnabled = true
                }
            }
            //permission to access location denied
            else {
                Toast.makeText(
                    requireContext(),
                    "This app requires location permissions to be granted",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }


}