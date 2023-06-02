package com.goridesnigeria.goridesrider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Reject_Ride_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reject_ride)
    }

    override fun onBackPressed() {
        finish()
        finishAffinity()
    }
}