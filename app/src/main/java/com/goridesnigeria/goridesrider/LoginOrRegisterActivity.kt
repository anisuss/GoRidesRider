package com.goridesnigeria.goridesrider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.goridesnigeria.goridesrider.databinding.ActivityLoginOrRegisterBinding
import com.goridesnigeria.goridesrider.utils.HelperClass

class LoginOrRegisterActivity : AppCompatActivity() {
    lateinit var biniding: ActivityLoginOrRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val helperClass = HelperClass()
        val user = FirebaseAuth.getInstance().currentUser

        biniding = DataBindingUtil.setContentView(
            this@LoginOrRegisterActivity,
            R.layout.activity_login_or_register
        )

        biniding.btnSignUp.setOnClickListener {
            startActivity(Intent(this@LoginOrRegisterActivity, SignupActivity::class.java))
        }
        biniding.btnLogin.setOnClickListener {
            startActivity(Intent(this@LoginOrRegisterActivity, OtpScreen::class.java))

        }

        Log.d(
            "Hamza",
            " I Phone=" + HelperClass().getRoomDAO(applicationContext)!!.getUserId()
                .toString()
        )

//        Check Login Firebase And Api
        if (HelperClass().getRoomDAO(applicationContext)!!.getUserId() != 0) {
            startActivity(Intent(this@LoginOrRegisterActivity, MainActivity::class.java))
            finish()
        }

//        if (HelperClass().getRoomDAO(applicationContext)!!.getUserId() != 0) {
//            startActivity(Intent(this@LoginOrRegisterActivity, MainActivity::class.java))
//            finish()
//        }

//        else{
//            val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//            val firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("OnlineDrivers").child(driverid)
//            firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        Toast.makeText(this@LoginOrRegisterActivity,"Exist"+ driverid, Toast.LENGTH_SHORT).show()
//                    }else{
//                        Toast.makeText(this@LoginOrRegisterActivity,"Not Exist"+driverid, Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this@LoginOrRegisterActivity,LoginActivity::class.java))
//                    }
//                }
//                override fun onCancelled(databaseError: DatabaseError) {
//                    Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
//                    Toast.makeText(this@LoginOrRegisterActivity,"Not Exist"+ driverid, Toast.LENGTH_SHORT).show()
//                }
//            })
////            startActivity(Intent(this@LoginOrRegisterActivity, MainActivity::class.java))
////            finish()
//        }
    }
}