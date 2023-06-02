package com.goridesnigeria.goridesrider

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.databinding.ActivityCarInspectionBinding

class CarInspectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityCarInspectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@CarInspectionActivity,
            R.layout.activity_car_inspection
        )
        binding.btnComplete.setOnClickListener {
            startActivity(Intent(this@CarInspectionActivity, MainActivity::class.java))
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}