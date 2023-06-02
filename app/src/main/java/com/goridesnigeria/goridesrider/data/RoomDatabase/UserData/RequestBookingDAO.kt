package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.*

@Dao
interface RequestBookingDAO {

    @Insert
    fun insert(requestBoking: RequestBookingDatabase)

    @Update
    fun update(requestBoking: RequestBookingDatabase)

    @Delete
    fun delete(requestBoking: RequestBookingDatabase)

    @Query("delete from Request_Booking")
    fun deletAll()



    @Query("SELECT * FROM Request_Booking WHERE id=:id")
    fun getRequestObject(id:Int):RequestBookingDatabase

}