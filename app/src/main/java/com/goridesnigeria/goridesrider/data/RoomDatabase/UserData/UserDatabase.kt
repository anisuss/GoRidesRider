package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User_table")
data class UserDatabase(
    @PrimaryKey(autoGenerate = false) val id: Int? = null,
    val name: String,
    val email: String,
    val city: String,
    val avatar: String="",
    val phone: String,
    val driving_licence_front_image: String="",
    val driving_licence_back_image: String="",
    val driving_licence: String="",
    val role: String
)