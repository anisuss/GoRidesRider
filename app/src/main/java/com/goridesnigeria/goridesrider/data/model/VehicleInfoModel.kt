package com.goridesnigeria.goridesrider.data.model

import com.google.gson.annotations.SerializedName

data class VehicleInfoModel(

    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("user_id")
    val user_id: Int = 0,
    @SerializedName("vehicle_type")
    val vehicle_type: String = "",
    @SerializedName("model")
    val model: String = "",
    @SerializedName("make_by")
    val make_by: String = "",
    @SerializedName("year")
    val year: String = "",
    @SerializedName("color")
    val color: String = "",
    @SerializedName("resgistration_number")
    val resgistration_number: String = "",
    @SerializedName("insurance_front")
    val insurance_front: String = "",
    @SerializedName("insurance_back")
    val insurance_back: String = "",
    @SerializedName("battery_working")
    val battery_working: String = "",
    @SerializedName("wipers_working")
    val wipers_working: String = "",
    @SerializedName("horn_working")
    val horn_working: String = "",
    @SerializedName("lights_working")
    val lights_working: String = "",
    @SerializedName("indicators_working")
    val indicators_working: String = "",
    @SerializedName("seating_capacity")
    val seating_capacity: String = ""

)