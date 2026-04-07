package com.example.ngo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ngo.Adapter.PaymentAdapter
import com.example.ngo.R
import com.example.ngo.Utils.PaymentDetails
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
class AdminHomeFragment : Fragment() {

    private lateinit var tvTotalFunds: TextView
    private lateinit var rvHistory: RecyclerView
    private lateinit var cardViewEvents: MaterialCardView
    private lateinit var database: DatabaseReference
    private val paymentList = mutableListOf<PaymentDetails>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        // Initialize Views
        tvTotalFunds = view.findViewById(R.id.tvTotalFunds)
        rvHistory = view.findViewById(R.id.rvPaymentHistory)
        cardViewEvents = view.findViewById(R.id.cardViewEvents)

        // Setup RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        database = FirebaseDatabase.getInstance().getReference("Donations")

        // Handle Navigation to Events List
        cardViewEvents.setOnClickListener {
//            val eventFragment = AdminViewEventsListFragment()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.nav_host_fragment_content_admin, eventFragment) // Replace with your actual container ID
//                .addToBackStack(null)
//                .commit()

            findNavController().navigate(R.id.adminViewEventsListFragment)
        }

        fetchHistory()
        return view
    }

    private fun fetchHistory() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                paymentList.clear()
                var sum = 0.0

                for (child in snapshot.children) {
                    val data = child.getValue(PaymentDetails::class.java)
                    data?.let {
                        paymentList.add(it)
                        // Removes non-numeric characters for calculation
                        val numericAmount = it.amount?.replace("[^\\d.]".toRegex(), "")?.toDoubleOrNull() ?: 0.0
                        sum += numericAmount
                    }
                }

                tvTotalFunds.text = "₹${String.format("%,.2f", sum)}"
                // Shows latest transactions at top
                rvHistory.adapter = PaymentAdapter(paymentList.asReversed())
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Call this manually if you need to populate Firebase with test data
    private fun seedDummyPayments() {
        val sdfDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dummyAmounts = listOf(100.0, 500.0, 1250.50, 50.0, 2000.0)

        for (i in 1..5) {
            val calendar = Calendar.getInstance()
            val paymentId = "pay_dummy_${UUID.randomUUID().toString().substring(0, 8)}"
            val amount = dummyAmounts.random()

            val dummyData = PaymentDetails(
                paymentId = paymentId,
                amount = "₹$amount",
                date = sdfDate.format(calendar.time),
                time = sdfTime.format(calendar.time),
                userUid = FirebaseAuth.getInstance().uid
            )
            database.push().setValue(dummyData)
        }
    }
}