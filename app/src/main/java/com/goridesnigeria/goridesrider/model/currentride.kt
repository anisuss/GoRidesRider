package com.goridesnigeria.goridesrider.model

class currentride {

    constructor()

    lateinit var pickupAddress: String
    lateinit var pickupLat: String
    lateinit var pickupLong: String
    lateinit var destinationAddress: String
    lateinit var destinationLat: String
    lateinit var destinationLong: String
//    lateinit var firstStopAddress: String
    //lateinit var firstStopLatLong: LatLng
//    lateinit var secondStopAddress: String
    // lateinit var secondStopLatLong: LatLng
    lateinit var carType: String
    //  lateinit var rideType: String
    var fare: String = ""
    lateinit var distance: String
    lateinit var time: String
    lateinit var passengerID: String
    var isRideAccepted = false

}