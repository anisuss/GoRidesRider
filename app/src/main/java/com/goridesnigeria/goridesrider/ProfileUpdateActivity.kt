package com.goridesnigeria.goridesrider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.goridesnigeria.goridesrider.databinding.ActivityProfileUpdateBinding
import com.goridesnigeria.goridesrider.utils.HelperClass

class ProfileUpdateActivity : AppCompatActivity() {

    val helperClass = HelperClass()

    lateinit var binding: ActivityProfileUpdateBinding

    lateinit var Name: TextView
    lateinit var phonenumber: TextView
    lateinit var driveremail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil.setContentView(this@ProfileUpdateActivity, R.layout.activity_profile_update)

        Name = findViewById(R.id.name)
//        phonenumber = findViewById(R.id.drivernumber)
//        driveremail = findViewById(R.id.driverpassword)

        val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()
//        Toast.makeText(this,"Driver Id" + driverid,Toast.LENGTH_LONG).show()
        val String = "https://gorides.ucstestserver.xyz/api/users/" + driverid
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        val request = JsonObjectRequest(Request.Method.GET, String, null, { response ->

            try {
                // on below line we are getting data from our response
                // and setting it in variables.
                val courseName: String = response.getString("name")
                val coursefirstlast: String = response.getString("name").get(0).toString()

//                val cpoursephone: String = response.getString("phone")
//                val email: String = response.getString("email")

                Name.text = courseName
                binding.first.text = coursefirstlast
//                phonenumber.text = cpoursephone
//                driveremail.text = email

            } catch (e: Exception) {
                // on below line we are
                // handling our exception.
                e.printStackTrace()
            }

        }, { error ->
            // this method is called when we get
            // any error while fetching data from our API
            Log.e("TAG", "RESPONSE IS $error")
            // in this case we are simply displaying a toast message.
            Toast.makeText(this, "Fail to get response"+error, Toast.LENGTH_SHORT)
                .show()
        })
        // at last we are adding
        // our request to our queue.
        queue.add(request)
            binding.back.setOnClickListener {
//                startActivity(Intent(this, MainActivity::class.java))
                finish()
//                finishAffinity()
}
        binding.termsandcondition.setOnClickListener({
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(false)

            dialog.setTitle("Terms and Conditions")
                .setMessage("Welcome to GoRides " +
                        "These terms and conditions outline the rules"+
                        "and regulations for the use of Gorides's"+
                        "By accessing this website we assume you accept these terms and conditions."+
                        "Do not continue to use goridesinc if you do not agree to take all of the terms and conditions stated on this page."+
                        "The following terminology applies to these Terms and Conditions,"+
                        "Privacy Statement and Disclaimer Notice and all Agreements: Client,"+
                        "You and Your refers to you, the person log on this website and compliant to the Company’s terms and conditions. " +
                        "The Company, Ourselves, We, Our and Us, refers to our Company."+
                        "Party, Parties, or Us, refers to both the Client and ourselves."+
                        "All terms refer to the offer, acceptance and consideration of payment necessary"+
                        "to undertake the process of our assistance to the Client in the most"+
                        "appropriate manner for the express purpose of meeting the Client’s"+
                        "needs in respect of provision of the Company’s stated services,"+
                        "in accordance with and subject to, prevailing law of Netherlands."+
                        "Any use of the above terminology or other words in the singular, plural,"+
                        "capitalization and or he/she or they, are taken as interchangeable and therefore as referring to same.")
                .setPositiveButton("OK") { dialog, whichButton ->

//                    finishAndRemoveTask()
                }
                .setNegativeButton("") { dialog, whichButton ->
                    // DO YOUR STAFF
//                     dialog.close()
                }

            dialog.show()
        })

        binding.promotion.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goridesinc.com/promotions/")))
        })

        binding.aboutus.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goridesinc.com/about-us/")))
        })

        binding.rateus.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ucs.gorides")))
        })

        binding.support.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goridesinc.com/support/")))
        })

        binding.cardone.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goridesinc.com/help-center/")))
        })

        binding.cardtwo.setOnClickListener({
            startActivity(Intent(this, MyWalletActivity::class.java))
//            finish()
//            finishAffinity()
        })
}

    override fun onBackPressed() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//        finishAffinity()
    }
}