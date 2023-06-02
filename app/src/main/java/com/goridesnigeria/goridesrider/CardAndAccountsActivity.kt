package com.goridesnigeria.gorides.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.databinding.ActivityCardAndAccountsBinding


class CardAndAccountsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCardAndAccountsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@CardAndAccountsActivity,
            R.layout.activity_card_and_accounts
        )

        binding.btnBack.setOnClickListener { finish() }

    }
}