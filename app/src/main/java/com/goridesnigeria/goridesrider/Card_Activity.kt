package com.goridesnigeria.goridesrider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Card_Activity : AppCompatActivity() {

//    lateinit var stripe: Stripe
//    val publishKey: String = "pk_test_51LTK5BA3HX1LLsVI94XfTW7CamHfDdIMNFgqWUeQTsQEGI2IZhK12R7hw0PtNGlvOiCCPG6XM6r49OU2tpvFpnwH00mtYwzke8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

//        stripe = Stripe(applicationContext, publishKey)

//        pay.setOnClickListener { OpenDialog() }
//    }

    }
}

//private fun OpenDialog() {
//            var dialog = Dialog(this)
//            dialog.setContentView(R.layout.activity_card)
//            var lp : WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
//                copyFrom(dialog.window.attributes)
//                width = WindowManager.LayoutParams.MATCH_PARENT
//                height = WindowManager.LayoutParams.WRAP_CONTENT
//            }
//            val submit = dialog.findViewById<View>(R.id.submit) as TextView
//            val cardNo = dialog.findViewById<View>(R.id.cardNo) as EditText
//            val month = dialog.findViewById<View>(R.id.month) as EditText
//            val year = dialog.findViewById<View>(R.id.year) as EditText
//            val cvv = dialog.findViewById<View>(R.id.cvv) as EditText
//
//    submit.setOnClickListener {
//        when {
//            cardNo.length() == 0 || month.length() == 0 || year.length() == 0 || cvv.length() == 0 ->
////                Toast.makeText(this, "Please fill all the fields"
////                    , Toast.LENGTH_SHORT).show()
////            cardNo.length() < 16 -> Toast.makeText(this, "Please enter" +
////                    " valid Card No.", Toast.LENGTH_SHORT).show()
//            else -> {
//                validateCard(cardNo.text.toString(), month.text.toString(), year.text.toString(), cvv.text.toString())
//                dialog.dismiss()
//            }
//        }
//    }
//    dialog.show()
//    dialog.getWindow().setAttributes(lp)
//}

//fun validateCard(card: String?, month: String?, year: String?, cvv: String?) {
//    val card = Card(card, Integer.valueOf(month), Integer.valueOf(year), cvv)
//    card.currency = "USD"
//    stripe.createToken(card, object : TokenCallback {
//        override fun onSuccess(token: Token?) {
//            Log.v("Token!","Token Created!!"+ token!!.getId())
//            Toast.makeText(this, "Token Created!!", Toast.LENGTH_SHORT).show()
//            chargeCard(token.id);
//        }
//
//        override fun onError(error: Exception?) {
////            Toast.makeText(this, error!!.message, Toast.LENGTH_SHORT).show()
//            error!!.printStackTrace()
//        }
//
//    })
//
//}
//
//private fun chargeCard(token: String?) {
//    // Pass that token, amount to your server using API to process payment.
//
//}