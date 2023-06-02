package com.goridesnigeria.goridesrider

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.databinding.ActivityRideDetailsBinding

class RideDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityRideDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this@RideDetailsActivity, R.layout.activity_ride_details)


        binding.btnBack.setOnClickListener { finish() }
        binding.txtRecipt.setOnClickListener {

            binding.txtSummary.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.txtSummary.setBackgroundColor(ContextCompat.getColor(this,R.color.greay))
            binding.llSummary.visibility=View.GONE

            binding.txtRecipt.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.txtRecipt.setBackgroundColor(ContextCompat.getColor(this,R.color.app_dark_green))
            binding.llReceipt.visibility=View.VISIBLE
        }


        binding.txtSummary.setOnClickListener {

            binding.txtSummary.setTextColor(ContextCompat.getColor(this,R.color.white))
            binding.txtSummary.setBackgroundColor(ContextCompat.getColor(this,R.color.app_dark_green))
            binding.llSummary.visibility=View.VISIBLE

            binding.txtRecipt.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.txtRecipt.setBackgroundColor(ContextCompat.getColor(this,R.color.greay))
            binding.llReceipt.visibility=View.GONE
        }


    }
}