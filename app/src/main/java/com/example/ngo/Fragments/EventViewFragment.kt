package com.example.ngo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ngo.R
import com.example.ngo.Utils.DonationEvent
import com.example.ngo.databinding.FragmentEventViewBinding
import com.google.firebase.database.*
class EventViewFragment : Fragment() {

    private var _binding: FragmentEventViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var currentEvent: DonationEvent? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventViewBinding.inflate(inflater, container, false)

        // 1. Get Event Data from Bundle
        currentEvent = arguments?.getParcelable("event_data")
        database = FirebaseDatabase.getInstance().getReference("DonationEvents")

        currentEvent?.let { event ->
            displayEventDetails(event)
            fetchDonorDetails(event.donorId)
            setupStatusButton(event)
        }

        return binding.root
    }

    private fun displayEventDetails(event: DonationEvent) {
        binding.tvDetailType.text = event.donationType
        binding.tvDetailDesc.text = event.description
        binding.tvDetailAddress.text = "📍 ${event.address}"
        binding.tvDetailDateTime.text = "🗓 ${event.date} at ${event.time}"

        updateButtonUI(event.eventStatus ?: false)

    }
    private fun fetchDonorDetails(donorId: String?) {
        if (donorId == null) return

        FirebaseDatabase.getInstance().getReference("Users").child(donorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Replace with your actual User data class fields
                    val name = snapshot.child("name").value.toString()
                    val phone = snapshot.child("phone").value.toString()

                    binding.tvDonorName.text = "Name: $name"
                    binding.tvDonorPhone.text = "Contact: $phone"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    private fun setupStatusButton(event: DonationEvent) {
        binding.btnUpdateStatus.setOnClickListener {
            val newStatus = !(event.eventStatus ?: false)
            val eventId = event.eventId ?: return@setOnClickListener

            // Update in Firebase
            database.child(eventId).child("eventStatus").setValue(newStatus)
                .addOnSuccessListener {
                    event.eventStatus = newStatus // Update local object
                    updateButtonUI(newStatus)
                    val msg = if (newStatus) "Event Verified!" else "Event Unverified!"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateButtonUI(isVerified: Boolean) {
        if (isVerified) {
            binding.btnUpdateStatus.text = "Mark as Unverified"
            binding.btnUpdateStatus.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
        } else {
            binding.btnUpdateStatus.text = "Mark as Verified"
            binding.btnUpdateStatus.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}