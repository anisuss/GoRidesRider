package com.goridesnigeria.goridesrider.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(

    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("driving_licence_front_image")
    val driving_licence_front_image: String = "",
    @SerializedName("driving_licence_back_image")
    val driving_licence_back_image: String = "",
    @SerializedName("driving_licence")
    val driving_licence: String = "",
    @SerializedName("role")
    val role: String = "",
    @SerializedName("vehicle")
    val vehicle: String = ""
)
