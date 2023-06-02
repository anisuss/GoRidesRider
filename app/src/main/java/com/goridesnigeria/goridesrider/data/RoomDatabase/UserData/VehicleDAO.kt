package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.*

@Dao
interface VehicleDAO {

    @Insert
    fun insert(vehicleDatabase: VehicleDatabase)

    @Update
    fun update(vehicleDatabase: VehicleDatabase)

    @Delete
    fun delete(vehicleDatabase: VehicleDatabase)

    @Query("delete from Vehicle_table")
    fun deletAllVehicleObejcts()

}