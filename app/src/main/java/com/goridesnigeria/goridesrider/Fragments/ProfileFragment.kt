package com.goridesnigeria.goridesrider.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.goridesnigeria.goridesrider.*
import com.goridesnigeria.goridesrider.data.model.UserModel
import com.goridesnigeria.goridesrider.databinding.FragmentProfileBinding
import com.goridesnigeria.goridesrider.utils.HelperClass


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentProfileBinding
    val helperClass = HelperClass()
    var userModel = UserModel()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
//        val number: TextView = binding.phone

//        val user = FirebaseAuth.getInstance().currentUser
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.btnPersonalInformation.setOnClickListener {
            startActivity(Intent(context, ProfileUpdateActivity::class.java))
        }

        binding.btnCardAccounts.setOnClickListener {
            startActivity(Intent(context, MyWalletActivity::class.java))
        }

//        binding.rateus.setOnClickListener({
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ucs.gorides")))
//        })
//
//        binding.termsandcondition.setOnClickListener({
//            startActivity(Intent(context, Terms_And_Condition::class.java))
//        })

//        binding.btntransaction.setOnClickListener{
//            (activity as AppCompatActivity?)!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container,TransactionHistoryFragment()).commit()
//        }

        binding.logout.setOnClickListener {

//            Signout user using Firebase Authentication and Database using Api.

//            FirebaseAuth.getInstance().signOut()
//            FirebaseAuth.getInstance().currentUser!!.delete()
//            firebaseAuth.signOut()

            val driverid = helperClass.getRoomDAO(requireContext())!!.getUserId().toString()
            requireContext().deleteDatabase("My_database")
            val data = FirebaseDatabase.getInstance().getReference("OnlineDrivers").child(driverid)
            data.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {
                            data.ref.removeValue()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            requireContext().startActivity(
                Intent(
                    requireContext(),
                    LoginOrRegisterActivity::class.java
                )
            )
            requireActivity().finish()
            requireActivity().finishAffinity()
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}