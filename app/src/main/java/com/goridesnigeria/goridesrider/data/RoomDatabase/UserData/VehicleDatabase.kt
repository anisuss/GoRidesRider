package com.goridesnigeria.goridesrider.data.RoomDatabase.UserData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Vehicle_table")
data class VehicleDatabase(
    @PrimaryKey(autoGenerate = false) val id: Int? = null,
    val user_id: String,
    val vehicle_type: String,
    val model: String,
    val make_by: String,
    val year: String,
    val color: String,
    val resgistration_number: String,
    val insurance_front: String,
    val insurance_back: String,
    val battery_working: String,
    val wipers_working: String,
    val horn_working: String,
    val lights_working: String,
    val indicators_working: String,
    val seating_capacity: String


)


