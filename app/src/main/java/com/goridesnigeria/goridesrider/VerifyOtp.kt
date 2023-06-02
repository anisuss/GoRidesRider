package com.goridesnigeria.goridesrider

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.goridesnigeria.goridesrider.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class VerifyOtp : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private var verificationID: String ?= null
    private var firebaseAuth: FirebaseAuth?= null
    private lateinit var edittextcode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_verify_otp)

        firebaseAuth = FirebaseAuth.getInstance()

        val button: Button = findViewById(R.id.verifybtn)
        edittextcode = findViewById(R.id.otp)

        val phoneno: String? = intent.getStringExtra("phone")
//        Toast.makeText(this,"Phone No"+phoneno, Toast.LENGTH_SHORT).show()
        Log.d(ContentValues.TAG,"PhoneNumberVerifyScreen"   + phoneno)

//        val pd = ProgressDialog(this)
//        pd.setMessage("Verifying.....")
//        pd.show()
//        pd.setCanceledOnTouchOutside(false)
//        pd.setCancelable(false)
//        getWindow().setFlags(
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (phoneno != null) {
            sendCode(phoneno)
        }

        button.setOnClickListener({

            if (edittextcode.text.toString().isEmpty()) {

                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();

            } else {

//                val pd = ProgressDialog(this)
//                pd.setMessage("Verifying.....")
//                pd.show()
//                pd.setCanceledOnTouchOutside(false)
//                pd.setCancelable(false)
                verifyCode(edittextcode.text.toString())

            }

//            val code: String = editText!!.text.toString()
//            if(edittextcode.text.toString().isEmpty()){
//                Toast.makeText(this,"Please Enter Code",Toast.LENGTH_SHORT).show()
//            }else if()
////            val code: String = editText!!.text.toString()
////            button.visibility = View.GONE
//        if (edittextcode.text.isEmpty()){
//            Toast.makeText(this,"Please Enter Code",Toast.LENGTH_SHORT).show()
//        }
//        else if (edittextcode.text.toString() != verifyCode(edittextcode.text.toString())) {
//            Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show()
//        }
//        else{
//            verifyCode(edittextcode.text.toString())
////            pd.dismiss()
////                progressdialog.dismiss()
//                Toast.makeText(this,"Code" +edittextcode,Toast.LENGTH_LONG).show()
//        }
        })
    }

    private fun sendCode(number: String){
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
            )
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationID = p0
        }
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            val code = p0!!.smsCode
            if (code!= null){
                edittextcode!!.setText(code)
                verifyCode(code)
                Log.d(ContentValues.TAG,"Coading"+code)
            }
        }
        override fun onVerificationFailed(p0: FirebaseException) {
//            Toast.makeText(this@OtpScreen,"dsfg"+p0,Toast.LENGTH_SHORT).show()
            Toast.makeText(this@VerifyOtp,"Code is InValid",Toast.LENGTH_SHORT).show()
        }

    }

    private fun verifyCode(code: String){
        val credential = PhoneAuthProvider.getCredential(verificationID!!,code)
        Log.d(ContentValues.TAG,"Verification Code"+credential)
        signIn(credential)
    }

    private fun signIn(credential: PhoneAuthCredential){
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener({task ->
            if (task.isSuccessful){
                startActivity(Intent(this,LoginActivity::class.java))
            }else{
                Toast.makeText(this@VerifyOtp,"Code is InValid",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        Toast.makeText(this,"Please Wait....",Toast.LENGTH_SHORT).show()
    }
}