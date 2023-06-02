package com.goridesnigeria.gorides.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.databinding.ActivityNotificationsBinding


class NotificationsActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@NotificationsActivity,
            R.layout.activity_notifications
        )
        binding.btnBack.setOnClickListener { finish() }
        binding.btnGoBack.setOnClickListener { finish() }
    }
}