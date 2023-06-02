package com.goridesnigeria.goridesrider.data.api

import com.goridesnigeria.goridesrider.data.model.UserModel
import com.goridesnigeria.goridesrider.data.model.VehicleInfoModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // @GET("api/v1/brand")
    @Multipart
    @POST("login")
    fun loginUser(
        @Part("email") email: RequestBody?,
        @Part("password") password: RequestBody?
    ): Call<UserModel>?

    @Multipart
    @POST("register")
    fun registerUserOrDriver(
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone_no: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("role") role: RequestBody?,
        @Part("city") city: RequestBody?,
        @Part("driving_licence") driving_licence: RequestBody?,
        @Part("driving_licence_front_image") driving_licence_front_image: RequestBody?,
        @Part("driving_licence_back_image") driving_licence_back_image: RequestBody?,
        @Part("avatar") avatar: RequestBody?
    ): Call<UserModel?>?

    @Multipart
    @POST("vehicle")
    fun registerVehicle(
        @Part("user_id") user_id: RequestBody?,
        @Part("vehicle_type") vehicle_type: RequestBody?,
        @Part("model") model: RequestBody?,
        @Part("make_by") make_by: RequestBody?,
        @Part("year") year: RequestBody?,
        @Part("color") color: RequestBody?,
        @Part("resgistration_number") resgistration_number: RequestBody?,
        @Part("insurance_front") insurance_front: RequestBody?,
        @Part("insurance_back") insurance_back: RequestBody?,
        @Part("battery_working") battery_working: RequestBody?,
        @Part("wipers_working") wipers_working: RequestBody?,
        @Part("horn_working") horn_working: RequestBody?,
        @Part("lights_working") lights_working: RequestBody?,
        @Part("indicators_working") indicators_working: RequestBody?,
        @Part("seating_capacity") seating_capacity: RequestBody?
    ): Call<VehicleInfoModel?>?

}