package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [UserDatabase::class,VehicleDatabase::class,RequestBookingDatabase::class], version = 3)

abstract class RoomMainClass : RoomDatabase() {
    abstract fun userDAO(): UserDAO
    abstract fun vehicle():VehicleDAO
    abstract fun requestBooking():RequestBookingDAO
}