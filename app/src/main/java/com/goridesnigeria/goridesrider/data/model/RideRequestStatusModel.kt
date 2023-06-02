package com.goridesnigeria.goridesrider.data.model

class RideRequestStatusModel {

    var IsRideAccepted: Boolean = false;
    var driverID: String = "";
    var rideStatus: String = "";
    var IsRideRejected: Boolean = false;

    constructor()

    constructor(IsRideAccepted: Boolean,IsRideRejected: Boolean, driverID: String, rideStatus: String) {
        this.IsRideAccepted = IsRideAccepted
        this.IsRideRejected = IsRideRejected
        this.driverID = driverID
        this.rideStatus = rideStatus
    }

}