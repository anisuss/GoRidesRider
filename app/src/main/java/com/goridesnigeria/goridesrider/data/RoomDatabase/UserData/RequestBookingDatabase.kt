package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Request_Booking")
class RequestBookingDatabase (
    val passengerID: String,
    val pickupAddress: String,
    val pickupLat: String,
    val pickupLong: String,
    val destinationAddress: String,
    val destinationLong: String,
    val destinationLat: String,
    val firstStopAddress: String,
    val firstStopLat: String,
    val firstStopLong: String,
    val secondStopAddress: String,
    val secondStopLat  :String,
    val secondStopLong: String,
    val carType: String,
    val rideType: String,
    val fare: String,
    val distance: String,
    val time: String,
    val isRideAccepted: Boolean = false,
    @PrimaryKey(autoGenerate = false) val id: Int? = null
)