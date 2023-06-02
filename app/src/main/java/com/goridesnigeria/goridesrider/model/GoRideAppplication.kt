package com.goridesnigeria.goridesrider.model

import android.app.Application

class GoRideAppplication(): Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object{
        lateinit var instance: GoRideAppplication
    }
}