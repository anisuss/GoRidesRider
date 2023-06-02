package com.goridesnigeria.goridesrider

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.CountryCodePicker
import com.goridesnigeria.goridesrider.databinding.ActivityLoginBinding

class OtpScreen : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var edittextcode:EditText
    private lateinit var clock: DigitalClock

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_screen)

        val ccp: CountryCodePicker = findViewById(R.id.ccp)
        val etphonenumber: EditText = findViewById(R.id.etPhoneNumber)
        ccp.registerCarrierNumberEditText(etphonenumber)
        val sendOtpBtn: Button = findViewById(R.id.sendotp)
        sendOtpBtn.setOnClickListener({

            if(checkValidity(ccp))
            {
                //Change with your own functionality
//                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,VerifyOtp::class.java)
                intent.putExtra("phone",ccp.fullNumberWithPlus)
                Log.d(TAG,"PhoneNumber"+ccp.fullNumberWithPlus)
                startActivity(intent)

            }else{
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun checkValidity(ccp: CountryCodePicker): Boolean {

        return if(ccp.isValidFullNumber) {
//            Toast.makeText(this, "number " + ccp.fullNumber + " is valid.", Toast.LENGTH_SHORT).show()
            true
        } else {
            false
        }
    }
}