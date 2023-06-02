package com.goridesnigeria.goridesrider.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goridesnigeria.gorides.adapter.RidesAdapter
import com.goridesnigeria.gorides.model.RideModel
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.databinding.FragmentRidesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var viewModel:RideModel
private lateinit var riderecyclerview: RecyclerView
lateinit var adapter: RidesAdapter
/**
 * A simple [Fragment] subclass.
 * Use the [RidesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RidesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentRidesBinding

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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rides, container, false)

//        binding.cardMore.setOnClickListener {
//            startActivity(
//                Intent(context, RideDetailsActivity::class.java)
//            )
//        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RidesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RidesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        riderecyclerview = view.findViewById(R.id.recyclerview)
        riderecyclerview.layoutManager = LinearLayoutManager(context)
        riderecyclerview.setHasFixedSize(true)
        adapter = RidesAdapter()
        riderecyclerview.adapter = adapter

        viewModel = ViewModelProvider(this).get(RideModel::class.java)
        viewModel._allrides.observe(viewLifecycleOwner, Observer {

            adapter.updateRideList(it)
        })
    }
}