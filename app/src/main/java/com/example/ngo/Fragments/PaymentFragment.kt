package com.example.ngo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ngo.R
import com.example.ngo.Utils.PaymentDetails
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private lateinit var etAmount: TextInputEditText
    private lateinit var btnDonate: MaterialButton

    private val database = FirebaseDatabase.getInstance().getReference("Donations")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        etAmount = view.findViewById(R.id.etAmount)
        btnDonate = view.findViewById(R.id.btnDonate)

        Checkout.preload(requireContext())

        btnDonate.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()

            if (amountStr.isEmpty()) {
                etAmount.error = "Please enter an amount"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0

            // Razorpay minimum amount is usually 1 INR (100 paise)
            if (amount < 1.0) {
                etAmount.error = "Minimum donation is ₹1"
            } else {
                startPayment(amount)
            }
        }

        return view
    }

    private fun startPayment(amount: Double) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_YOUR_KEY_ID")

        try {
            val options = JSONObject()
            options.put("name", "NGO Donation")
            options.put("description", "Supporting the cause")
            options.put("currency", "INR")
            // amount in paise
            options.put("amount", (amount * 100).toInt())

            val prefill = JSONObject()
            prefill.put("email", auth.currentUser?.email ?: "donor@example.com")
            options.put("prefill", prefill)

            checkout.open(requireActivity(), options)

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun onPaymentSuccess(paymentId: String?) {
        val amountValue = etAmount.text.toString()
        val calendar = Calendar.getInstance()

        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)
        val uid = auth.currentUser?.uid ?: "Anonymous"

        val details = PaymentDetails(
            paymentId = paymentId,
            amount = "₹$amountValue",
            date = date,
            time = time,
            userUid = uid
        )

        val donationKey = database.push().key
        if (donationKey != null) {
            database.child(donationKey).setValue(details).addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(context, "Donation Saved to Firebase", Toast.LENGTH_SHORT).show()
                    etAmount.text?.clear()
                }
            }
        }
    }

    fun onPaymentError(code: Int, response: String?) {
        if (isAdded) {
            Toast.makeText(context, "Payment Failed or Cancelled", Toast.LENGTH_LONG).show()
        }
    }
}