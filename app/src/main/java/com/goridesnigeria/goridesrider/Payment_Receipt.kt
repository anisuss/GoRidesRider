package com.goridesnigeria.goridesrider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Payment_Receipt : AppCompatActivity() {

    lateinit var btn: Button
    lateinit var am: TextView
    lateinit var da: TextView
    lateinit var na: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_receipt)

        val balance: String? = intent.getStringExtra("balance")
        val name: String? = intent.getStringExtra("name")
        val dateandtime: String? = intent.getStringExtra("DateAndTime")


        btn = findViewById(R.id.close)
        am = findViewById(R.id.amount)
        da = findViewById(R.id.date)
        na = findViewById(R.id.name)

        am.setText(balance)
        da.setText(dateandtime)
        na.setText(name)

        btn.setOnClickListener({

//            Toast.makeText(this,"Closed Transaction Receipt",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MyWalletActivity::class.java))
//            finish()
//            finishAffinity()
        })
    }
    override fun onBackPressed() {

    }
}