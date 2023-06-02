package com.goridesnigeria.goridesrider.utils

import android.content.Context
import androidx.room.Room
import com.goridesnigeria.goridesrider.data.RoomDatabase.UserData.RequestBookingDAO
import com.goridesnigeria.goridesrider.data.RoomDatabase.UserData.RoomMainClass
import com.goridesnigeria.goridesrider.data.RoomDatabase.UserData.UserDAO
import java.util.regex.Pattern


class HelperClass {

    constructor()

    private fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun getRoomDAO(context: Context?): UserDAO? {
        val db: RoomMainClass =
            Room.databaseBuilder(context!!, RoomMainClass::class.java, "My_database")
                .allowMainThreadQueries()
                //.fallbackToDestructiveMigration()
                .build()
        return db.userDAO()
    }
    fun getRequestUserDAO(context: Context?): RequestBookingDAO? {
        val db: RoomMainClass =
            Room.databaseBuilder(context!!, RoomMainClass::class.java, "My_database")
                .allowMainThreadQueries()
                //.fallbackToDestructiveMigration()
                .build()
        return db.requestBooking()
    }

}