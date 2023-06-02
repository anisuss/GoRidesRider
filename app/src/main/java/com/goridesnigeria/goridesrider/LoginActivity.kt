package com.goridesnigeria.goridesrider

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.data.RoomDatabase.UserData.UserDatabase
import com.goridesnigeria.goridesrider.data.api.ApiClient
import com.goridesnigeria.goridesrider.data.api.ApiService
import com.goridesnigeria.goridesrider.data.model.UserModel
import com.goridesnigeria.goridesrider.databinding.ActivityLoginBinding
import com.goridesnigeria.goridesrider.utils.HelperClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var apiService: ApiService
    val helperClass = HelperClass()
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.edtPassword.setHint("Enter Password")

//        setupPermissions()
//        if (check(this@LoginActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Log.d("SHAN", "onCreate: permissions granted")
//        } else {
//            ask(this@LoginActivity, Manifest.permission.ACCESS_FINE_LOCATION)
//        }

        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                loginApiCall()
//                PhoneNoOtp()
            }
        }
    }

    fun loginApiCall() {
        binding.progressView.setVisibility(View.VISIBLE)
        apiService = ApiClient.getClient(this)!!.create(ApiService::class.java)
        val email: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtEmail.text.toString())
        val password: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtPassword.text.toString())
        apiService.loginUser(email, password)!!.enqueue(object :
            retrofit2.Callback<UserModel?> {
            override fun onResponse(
                call: Call<UserModel?>,
                response: Response<UserModel?>
            ) {
                runOnUiThread(Runnable {
                    binding.progressView.setVisibility(View.INVISIBLE)
                    val userModel: UserModel? = response.body()
                    if (userModel != null) {
                        val userDatabase = UserDatabase(
                            userModel.id,
                            userModel.name,
                            userModel.email,
                            userModel.city,
                            "userModel.avatar",
                            userModel.phone,
                            "userModel.driving_licence_front_image",
                            " userModel.driving_licence_back_image",
                            "userModel.driving_licence",
                            userModel.role
                        )
                        helperClass.getRoomDAO(applicationContext)?.insert(userDatabase)
//                        PhoneNoOtp()
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                        )
                        Log.d("SHAN", "userModel" + call + "  " + userModel)
                    } else {
                        Log.d("SHAN", "Model null" + call + "  " + response)
                        binding.progressView.setVisibility(View.INVISIBLE)
                        Toast.makeText(this@LoginActivity,"Please Check Details",Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(call: Call<UserModel?>, t: Throwable) {
                Log.d("SHAN", "Model onFailure" + call + " " + t)
                binding.progressView.setVisibility(View.INVISIBLE)
            }
        })
    }

    fun validateForm(): Boolean {
        if (!isValidEmail(binding.edtEmail.text.toString())) {
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtPassword.text?.isEmpty() == true) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (binding.edtPhone.text.isEmpty()){
//            Toast.makeText(this,"Enter Phone Number",Toast.LENGTH_SHORT).show()
//            return false
//        }else{
////            val phone = binding.edtPhone.text.toString()
////            val intent = Intent(this,OtpScreen::class.java)
////            intent.putExtra("phone",phone)
////            startActivity(intent)
//        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (check(activity = this, permissions = permissions)) {
//            Log.d("SHAN", "onRequestPermissionsResult: Permissions granted after dialog")
//        } else {
//            Log.d("SHAN", "onRequestPermissionsResult: Permissions rejected after dialog")
//        }
//    }
private fun setupPermissions() {

    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage("Permission to access the Location is required for this app to Use Taxi.")
            .setTitle("Go Rides")
        builder.setPositiveButton(
            "OK"
        )
        { dialog, id ->
            Log.i(TAG, "Clicked")
            makeRequest()
        }
        val dialog = builder.create()
        dialog.show()
    }
    else{
//           Toast.makeText(this,"dsfg",Toast.LENGTH_LONG).show()
    }

//        val permission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i("SHAN", "Permission to record denied")
//            val builder = AlertDialog.Builder(this)
//                builder.setCancelable(false)
//                builder.setMessage("Permission to access the Location is required for this app to Use Taxi.")
//                .setTitle("Go Rides")
//                builder.setPositiveButton(
//                    "OK"
//                            )
//                    { dialog, id ->
//                        Log.i(TAG, "Clicked")
//                    }
//                        val dialog = builder.create()
//                        dialog.show()
//                        makeRequest()
//        }
}

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            RECORD_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        finishAffinity()
    }

//    fun PhoneNoOtp() {
//
//        val phone = binding.edtPhone.text.toString()
//        val intent = Intent(this,OtpScreen::class.java)
//        intent.putExtra("phone",phone)
//        startActivity(intent)
//    }
}