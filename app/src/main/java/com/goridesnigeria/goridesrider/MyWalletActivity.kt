package com.goridesnigeria.goridesrider

//import com.stripe.Stripe
//import com.stripe.android.TokenCallback
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.goridesrider.databinding.ActivityMyWalletBinding
import com.goridesnigeria.goridesrider.model.TransactionReceipt
import com.goridesnigeria.goridesrider.utils.HelperClass
import java.text.SimpleDateFormat
import java.util.*

class MyWalletActivity : AppCompatActivity() {

    val helperClass = HelperClass()
    lateinit var databasereference: DatabaseReference
    lateinit var binding: ActivityMyWalletBinding
    var database = Firebase.database
    var myRef = database.getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(
                this@MyWalletActivity,
            R.layout.activity_my_wallet)

        val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()

        val pd = ProgressDialog(this)
        pd.setMessage("loading.....")
        pd.show()
        pd.setCanceledOnTouchOutside(false)
        pd.setCancelable(false)
        val String = "https://gorides.ucstestserver.xyz/api/users/" + driverid
//        Driver Id Name Get
        val RequestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, String, null, { response ->
            try {
                val courseName: String = response.getString("name")
                val email: String = response.getString("email")
                binding.drivername.text = courseName
                binding.drivername.setText(courseName)
                pd.dismiss()
//                Toast.makeText(context, "Response " +courseName, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Log.e("TAG", "RESPONSE IS $error")
//            Toast.makeText(context, "Fail to get response"+error, Toast.LENGTH_SHORT)
//                .show()
        })
        RequestQueue.add(request)

//            if (binding.driverbalance.text.toString() == "0") {
//                binding.paynow.visibility = android.view.View.GONE
//            } else {
//                binding.paynow.visibility = android.view.View.VISIBLE
//            }

        binding.paynow.setOnClickListener({

            if (binding.driverbalance.text.toString() == "0.00"){

                Toast.makeText(this,"No Pending Balance",Toast.LENGTH_SHORT).show()

            }else {
                makePayment()

//                startActivity(Intent(this,receipt_Activity::class.java))

            }
        })
        databasereference = FirebaseDatabase.getInstance().getReference("DriverPayment")
            .child(helperClass.getRoomDAO(this)!!.getUserId().toString())

        databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var sum = 0
                for (data in dataSnapshot.children) {
                    sum += data.child("fare").getValue().toString().toInt()
                    binding.driverbalance.text = sum.toString()
                    Log.d(ContentValues.TAG, "Total Fare Value" + sum * 25 / 100)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }

        })

        binding.backbtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            finishAffinity()
        }

    }

    fun makePayment() {
        RaveUiManager(this)
            .setAmount(binding.driverbalance.text.toString().toInt().toDouble())
            .setEmail("")
            .setCountry("NG")
            .setCurrency("NGN")
            .setfName(binding.drivername.text.toString())
            .setlName(binding.drivername.text.toString())
            .setNarration("Drive Provide Company Payment")
            .setPublicKey("FLWPUBK-2b0677e4f739577c6842ded57bd26b89-X")
            .setEncryptionKey("04075965eb1bcbfd200397ea")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .acceptCardPayments(true)
            .onStagingEnv(false)
            .shouldDisplayFee(true)
            .showStagingLabel(true)
            .initialize()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "Transaction Successfully", Toast.LENGTH_LONG).show()

//                All Code Working
                val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val currentDateandTime: String = sdf.format(Date())

                var paymentReceipt = TransactionReceipt()

                paymentReceipt.Amount = binding.driverbalance.text.toString()
                paymentReceipt.DateAndTime = currentDateandTime
                paymentReceipt.Status = "Transaction Successfully"
                paymentReceipt.passengerName = binding.drivername.text.toString()

                myRef.child("DriverPaymentReceipt").child(driverid).push().setValue(paymentReceipt)

                val intent = Intent(this,Payment_Receipt::class.java)
                intent.putExtra("balance",binding.driverbalance.text.toString())
                intent.putExtra("name",binding.drivername.text.toString())
                intent.putExtra("DateAndTime",currentDateandTime)
                startActivity(intent)
                finish()
                finishAffinity()
                val data = FirebaseDatabase.getInstance().getReference("DriverPayment").child(driverid)
                data.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {
                            data.ref.removeValue()
                            binding.driverbalance.setText("0")

////                            requireActivity().stopService(
////                                Intent(
////                                    requireContext(),
////                                    DriverLocationUpdateService::class.java
////                                )
////                            )
                        }
                    }
//
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


//                databasereference = FirebaseDatabase.getInstance().getReference("DriverPayment")
//                    .child(helperClass.getRoomDAO(this)!!.getUserId().toString())
//                databasereference.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
////                        var sum = 0
//                        for (data in dataSnapshot.children) {
//                            data.ref.removeValue()
////                            sum += data.child("fare")
////                            binding.driverbalance.text = sum.toString() + " ₦"
////                            Log.d(ContentValues.TAG, "Total Fare Value" + sum * 25 / 100)
//                        }
//                    }
//                    override fun onCancelled(databaseError: DatabaseError) {
//                    }
//
//                })
//                Toast.makeText(this,"Amount" +RavePayManager(this)
//                    .setAmount(binding.driverbalance.toString().toDouble()),Toast.LENGTH_SHORT).show()

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "Service Is Not Available", Toast.LENGTH_LONG).show()
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onBackPressed() {

    }
}