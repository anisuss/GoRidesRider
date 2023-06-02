package com.goridesnigeria.goridesrider

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.goridesnigeria.gorides.adapter.MessageAdapter
import com.goridesnigeria.gorides.adapter.PassengerChatAdapter
import com.goridesnigeria.gorides.model.MessageModel
import com.goridesnigeria.gorides.model.passengerMessageModel
import com.goridesnigeria.goridesrider.R
import com.goridesnigeria.goridesrider.data.model.RideRequestStatusModel
import com.goridesnigeria.goridesrider.model.message
import com.goridesnigeria.goridesrider.utils.HelperClass

class MessageActivity : AppCompatActivity() {

    val helperClass = HelperClass()
    var rideRequestStatus = RideRequestStatusModel(true,false,"0","pending")
    var database = Firebase.database
    var myRef = database.getReference()

    private lateinit var viewModel: MessageModel
    private lateinit var riderecyclerview: RecyclerView
    lateinit var adapter: MessageAdapter

    private lateinit var passengerchatviewModel: passengerMessageModel
    private lateinit var passengerchatrecyclerview: RecyclerView
    lateinit var passengeradapter: PassengerChatAdapter


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val back: ImageButton = findViewById(R.id.back)
        val etmessage: EditText = findViewById(R.id.entermessage)
        val send: ImageButton = findViewById(R.id.send)

        //        Driver Chat
        riderecyclerview = findViewById(R.id.recyclerview)
        riderecyclerview.layoutManager = LinearLayoutManager(this)
        riderecyclerview.setHasFixedSize(true)
        adapter = MessageAdapter()
        riderecyclerview.adapter = adapter

        viewModel = ViewModelProvider(this).get(MessageModel::class.java)
        viewModel._allmessages.observe(this, Observer {

            adapter.updateMessageList(it)

        })

        riderecyclerview.isNestedScrollingEnabled = false
        riderecyclerview.setOnTouchListener { _, _ -> true }

//     Passenger Chat Code
        riderecyclerview = findViewById(R.id.tworecyclerview)
        riderecyclerview.layoutManager = LinearLayoutManager(this)
        riderecyclerview.setHasFixedSize(true)
        passengeradapter = PassengerChatAdapter()
        riderecyclerview.adapter = passengeradapter

        passengerchatviewModel = ViewModelProvider(this).get(passengerMessageModel::class.java)
        passengerchatviewModel._allpassengermessages.observe(this, Observer {

            passengeradapter.updatePassengerMessageList(it)

        })


        send.setOnClickListener({

//            val text = etmessage.text.toString()
//            Log.d(TAG,"Driver Side Message Send" +text)

            val text = etmessage.text.toString()
            val mes = message()
            mes.Message = text.toString()
//            val driverid = helperClass.getRoomDAO(applicationContext)!!.getUserId().toString()

            var objectt = helperClass.getRequestUserDAO(this)!!
                .getRequestObject(helperClass.getRoomDAO(applicationContext)!!.getUserId().toInt())
            val myRef = database.getReference().child("RideRequest").child(objectt.passengerID)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    rideRequestStatus = dataSnapshot.getValue(RideRequestStatusModel::class.java)!!
                    Log.d("Hamza Zulfiqar", "Valuse get=" + rideRequestStatus.rideStatus)

                    FirebaseDatabase.getInstance().getReference().child("Chats").child(objectt.passengerID).push().setValue(mes)
                    etmessage.setText("")
                    Log.d(TAG,"Text Message" + text)
                    Log.d(TAG,"Passenger Message Send Driver & Get Driver ID" +
                            FirebaseDatabase.getInstance().getReference().child("chats").child(objectt.passengerID))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Hamza Zulfiqar", "loadPost:onCancelled", databaseError.toException())
                }
            }
            myRef.addValueEventListener(postListener)
        })

        back.setOnClickListener({

            startActivity(Intent(this,ConfirmPickupActivity::class.java))
            finish()

        })
    }

    override fun onBackPressed() {
        Toast.makeText(this,"Please Use Application Back Button",Toast.LENGTH_SHORT).show()
    }
}