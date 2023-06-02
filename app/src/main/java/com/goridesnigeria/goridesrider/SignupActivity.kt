package com.goridesnigeria.goridesrider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.goridesnigeria.goridesrider.data.api.ApiClient
import com.goridesnigeria.goridesrider.data.api.ApiService
import com.goridesnigeria.goridesrider.data.model.UserModel
import com.goridesnigeria.goridesrider.databinding.ActivitySignupBinding
import com.goridesnigeria.goridesrider.utils.HelperClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    lateinit var apiService: ApiService
    lateinit var ImageUri: Uri

    //    lateinit var image: Uri
    val helperClass = HelperClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@SignupActivity, R.layout.activity_signup)

//        binding.imgDfront.setOnClickListener({
//            var intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(intent, 123)
//        })

//        binding.imgBack.setOnClickListener(
//            {
//                var intent = Intent()
//                intent.type = "image/*"
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(intent,1)
//            }
//        )

        binding.btnNext.setOnClickListener {
            if (validateForm()) {
                binding.progressView.setVisibility(View.VISIBLE)
                apiService = ApiClient.getClient(this)!!.create(ApiService::class.java)
//                uploadImage()
                val firstName: RequestBody = RequestBody.create(
                    MultipartBody.FORM,
                    binding.edtFirstName.text.toString().plus(binding.edtLastName.text.toString())
                )
                val phoneNumber: RequestBody =
                    RequestBody.create(MultipartBody.FORM, binding.edtPhoneNumber.text.toString())
                val city: RequestBody =
                    RequestBody.create(MultipartBody.FORM, binding.edtCity.text.toString())
                val drivingLicence: RequestBody = RequestBody.create(
                    MultipartBody.FORM,
                    binding.edtDrivingLicence.text.toString()
                )
                val email: RequestBody =
                    RequestBody.create(MultipartBody.FORM, binding.edtEmail.text.toString())
                val password: RequestBody =
                    RequestBody.create(MultipartBody.FORM, binding.edtPassword.text.toString())
//        val licencefront: RequestBody = RequestBody.create(MultipartBody.FORM,binding.imgDfront.setImageURI(ImageUri).toString())
                val role: RequestBody = RequestBody.create(MultipartBody.FORM, "Driver")
                apiService.registerUserOrDriver(
                    firstName,
                    email,
                    phoneNumber,
                    password,
                    role,
                    city,
                    drivingLicence,
                    null,
                    null,
                    null
                )!!.enqueue(object : Callback<UserModel?> {
                    override fun onResponse(
                        call: Call<UserModel?>,
                        response: Response<UserModel?>
                    ) {
                        runOnUiThread(Runnable {
                            binding.progressView.setVisibility(View.INVISIBLE)
                            val userModel: UserModel? = response.body()
                            if (userModel != null) {
//                                uploadImage()
                                startActivity(
                                    Intent(
                                        this@SignupActivity,
                                        SignupCarInformationActivity::class.java
                                    )
                                        .putExtra("userID", userModel.id.toString())
                                        .putExtra("name", userModel.name.toString())
                                        .putExtra("number", userModel.phone.toString())
                                        .putExtra("pass", userModel.email.toString())

                                )

                                Log.d("SHAN", "" + userModel.name)

                            } else {

                                Log.d("SHAN", "Model null" + call + "  " + response)
                                binding.progressView.setVisibility(View.INVISIBLE)
                                Toast.makeText(
                                    this@SignupActivity,
                                    "Driver Already Create Account",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }

                    override fun onFailure(call: Call<UserModel?>, t: Throwable) {
                        Log.d("SHAN", "Model onFailure" + call + " " + t)
                        binding.progressView.setVisibility(View.INVISIBLE)
                    }
                })
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }


    fun validateForm(): Boolean {

        if (binding.edtFirstName.text.isEmpty()) {
            Toast.makeText(this, "Enter first name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtLastName.text.isEmpty()) {
            Toast.makeText(this, "Enter last name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.edtPhoneNumber.text.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtCity.text.isEmpty()) {
            Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtDrivingLicence.text.isEmpty()) {
            Toast.makeText(this, "Enter driving licence number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isValidEmail(binding.edtEmail.text.toString())) {
            Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtPassword.text.isEmpty()) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (binding.imgDfront == null){
//            Toast.makeText(this, "Add your profile Image", Toast.LENGTH_SHORT).show()
//            return false
//        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    fun uploadImage() {

//            val license = binding.imgDfront.setImageURI(null).toString()
//        val email = binding.edtPhoneNumber.text.toString()
//        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
//        val now = Date()
//        val filename = formatter.format(now)

//        val firebasestorage = FirebaseStorage.getInstance().getReference("DriveProfileImages/$email/$filename")
//        val realtime = FirebaseDatabase.getInstance().getReference().child("DriveProfileImages").child(email).push()
//        val hashmap: HashMap<String, String> = HashMap()
//        hashmap.put("profileImage", filename)
//        realtime.setValue(hashmap)
//
//            firebasestorage.child(email).putFile(ImageUri).
//
//            addOnSuccessListener {
//                binding.imgDfront.setImageURI(null)
//            }.addOnFailureListener {
//            }
    }
//            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
//            val now = Date()
//            val filename = formatter.format(now)
//
//            val firebasestorage = FirebaseStorage.getInstance().getReference("images/$email/$filename")
//            val realtime = FirebaseDatabase.getInstance().getReference().child("Images").child(email)
//            val hashmap:HashMap<String ,String> = HashMap()
//            hashmap.put("imageUrlLicense",filename)
//            realtime.setValue(hashmap)
//
//            firebasestorage.child(email).putFile(ImageUri).
//
//            addOnSuccessListener {
//                binding.imgDfront.setImageURI(null)
//
//            }.addOnFailureListener {
//
//            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            ImageUri = data?.data!!
//            binding.imgDfront.setImageURI(ImageUri)
        }
    }
}
//        if (requestCode == 1){
//            ImageUri = data?.data!!
//            image = data?.data!!
//            binding.imgDfront.setImageURI(ImageUri)
//            binding.imgBack.setImageURI(image)
//        }

//            val driverid = helperClass.getRoomDAO(this)!!.getUserId().toString()
//            ImageUri = data?.data!!
//            binding.imgDfront.setImageURI(ImageUri)
//            val realtime = FirebaseDatabase.getInstance().getReference().child("Images").child(driverid)
//            val hashmap:HashMap<String ,String> = HashMap()
//            hashmap.put("imageUrl",ImageUri.toString())
//            realtime.setValue(hashmap)
//            Toast.makeText(this,"fcngvmbjnkml",Toast.LENGTH_SHORT).show()
//            val imagename:StorageReference = FirebaseStorage.getInstance().getReference().child(driverid)

//        }
//    }