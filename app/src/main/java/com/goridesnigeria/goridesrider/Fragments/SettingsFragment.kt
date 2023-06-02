package com.goridesnigeria.goridesrider.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.goridesnigeria.goridesrider.MyWalletActivity
import com.goridesnigeria.goridesrider.ProfileActivity
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.databinding.FragmentSettingsBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)


        binding.txtProfile.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }
        binding.txtWallet.setOnClickListener {
            startActivity(Intent(context, MyWalletActivity::class.java))
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}