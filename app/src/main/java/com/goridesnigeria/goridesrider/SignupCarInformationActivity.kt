package com.goridesnigeria.goridesrider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.data.api.ApiClient
import com.goridesnigeria.goridesrider.data.api.ApiService
import com.goridesnigeria.goridesrider.data.model.VehicleInfoModel
import com.goridesnigeria.goridesrider.databinding.ActivitySignupCarInformationBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupCarInformationActivity : AppCompatActivity() {
     lateinit var userID: String
    lateinit var apiService: ApiService
    var seatingCapacity: String = "Select car seating capacity"
    var batteryTerminal: String = "Select all options"
    var windScreen: String = "Select all options"
    var hornWork: String = "Select all options"
    var lightsWork: String = "Select all options"
    var indicatorsWork: String = "Select all options"
    lateinit var binding: ActivitySignupCarInformationBinding
    lateinit var radioButton: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@SignupCarInformationActivity,
            R.layout.activity_signup_car_information
        )

        userID = intent.getStringExtra("userID").toString()
//        Toast.makeText(this, "userID=" + userID, Toast.LENGTH_SHORT).show()
        binding.rlCarInspection.visibility = View.GONE
        binding.btnNext.setOnClickListener {
            if (validateForm()) {
                binding.rlCarInspection.visibility = View.VISIBLE
            }
        }
        binding.btnBackInspection.setOnClickListener {
            binding.rlCarInspection.visibility = View.GONE
        }
        binding.btnBack.setOnClickListener { finish() }
        binding.radioGroup1.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            seatingCapacity = radioButton.getText().toString();
        }
        )
        binding.rgBattery.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            batteryTerminal = radioButton.getText().toString();
        }
        )
        binding.rgWiper.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            windScreen = radioButton.getText().toString();
        }
        )
        binding.rgHorn.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            hornWork = radioButton.getText().toString();
        }
        )
        binding.rgLights.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            lightsWork = radioButton.getText().toString();
        }
        )
        binding.rgIndicators.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            radioButton = findViewById<View>(checkedId) as RadioButton
            indicatorsWork = radioButton.getText().toString();
        }
        )

        binding.btnComplete.setOnClickListener {
            if (validateInspectionForm()) {

                apiCall();
            }

        }
    }

    fun apiCall() {
        binding.progressView.setVisibility(View.VISIBLE)
        val vModel: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtVmodel.text.toString())
        val vYear: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtVyear.text.toString())
        val vColor: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtYcolor.text.toString())
        val vRegNo: RequestBody =
            RequestBody.create(MultipartBody.FORM, binding.edtVregno.text.toString())
        val sitCapacity: RequestBody = RequestBody.create(MultipartBody.FORM, seatingCapacity)

        val cTerminal: RequestBody = RequestBody.create(MultipartBody.FORM, batteryTerminal)
        val cWindScreeb: RequestBody = RequestBody.create(MultipartBody.FORM, windScreen)
        val cHorn: RequestBody = RequestBody.create(MultipartBody.FORM, hornWork)
        val cLight: RequestBody = RequestBody.create(MultipartBody.FORM, lightsWork)
        val cIndicators: RequestBody = RequestBody.create(MultipartBody.FORM, indicatorsWork)
        val userID: RequestBody = RequestBody.create(MultipartBody.FORM, userID)
        apiService = ApiClient.getClient(this)!!.create(ApiService::class.java)
        apiService.registerVehicle(
            userID,
            null,
            vModel,
            null,
            vYear,
            vColor,
            vRegNo,
            null,
            null,
            cTerminal,
            cWindScreeb,
            cHorn,
            cLight,
            cIndicators,sitCapacity
        )!!.enqueue(object :
            Callback<VehicleInfoModel?> {
            override fun onResponse(
                call: Call<VehicleInfoModel?>,
                response: Response<VehicleInfoModel?>
            ) {
                runOnUiThread(Runnable {
                    binding.progressView.setVisibility(View.INVISIBLE)
                    val userModel: VehicleInfoModel? = response.body()
                    if (userModel != null) {
                        startActivity(Intent(this@SignupCarInformationActivity, LoginOrRegisterActivity::class.java))
                        Toast.makeText(applicationContext, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                        finishAffinity()
                        Log.d("SHAN", "userModel" + call + "  " + userModel)
                    } else {
                        Log.d("SHAN", "Model null" + call + "  " + response)
                        binding.progressView.setVisibility(View.INVISIBLE)

//                        After Complete Signup to Move Login Activity Using Api Call.
//                        startActivity(
//                            Intent(
//                                this@SignupCarInformationActivity,
//                                LoginActivity::class.java ))
                           Toast.makeText(applicationContext, "Account Not Created", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(call: Call<VehicleInfoModel?>, t: Throwable) {
                Log.d("SHAN", "Model onFailure" + call + " " + t)
                binding.progressView.setVisibility(View.INVISIBLE)


            }
        })
    }


    fun validateForm(): Boolean {

        if (binding.edtVmodel.text.isEmpty()) {
            Toast.makeText(this, "Enter vehicle model", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtVyear.text.isEmpty()) {
            Toast.makeText(this, "Enter vehicle year", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.edtYcolor.text.isEmpty()) {
            Toast.makeText(this, "Enter vehicle color", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtVregno.text.isEmpty()) {
            Toast.makeText(this, "Enter vehicle Reg. No", Toast.LENGTH_SHORT).show()
            return false
        }
        if (seatingCapacity.equals("Select car seating capacity")) {
            Toast.makeText(this, "Select car seating capacity", Toast.LENGTH_SHORT).show()
            return false
        }



        return true
    }

    fun validateInspectionForm(): Boolean {
        if (batteryTerminal.equals("Select all options")) {
            Toast.makeText(this, "Select all options", Toast.LENGTH_SHORT).show()
            return false
        }
        if (windScreen.equals("Select all options")) {
            Toast.makeText(this, "Select all options", Toast.LENGTH_SHORT).show()
            return false
        }
        if (hornWork.equals("Select all options")) {
            Toast.makeText(this, "Select all options", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lightsWork.equals("Select all options")) {
            Toast.makeText(this, "Select car seating capacity", Toast.LENGTH_SHORT).show()
            return false
        }
        if (indicatorsWork.equals("Select all options")) {
            Toast.makeText(this, "Select all options", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onBackPressed() {

    }
}