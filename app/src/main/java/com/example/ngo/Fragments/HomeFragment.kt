package com.example.ngo.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.ngo.Adapters.CertificateAdapter
import com.example.ngo.Utils.Certificate
import com.example.ngo.Utils.DonationEvent
import com.example.ngo.databinding.DialogAddDonationBinding
import com.example.ngo.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var certAdapter: CertificateAdapter
    private val certificateList = mutableListOf<Certificate>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCertificateCarousel()
        fetchCertificates()

        binding.addEvent.setOnClickListener {
            showAddDonationDialog()
        }
    }

    private fun setupCertificateCarousel() {
        // Initialize adapter with a delete callback
        certAdapter = CertificateAdapter(certificateList)
        binding.rvCertificates.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = certAdapter

            // Adds the "Snap" effect to the carousel
            if (onFlingListener == null) {
                PagerSnapHelper().attachToRecyclerView(this)
            }
        }
    }

    private fun fetchCertificates() {
        val ref = FirebaseDatabase.getInstance().getReference("Certificates")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                certificateList.clear()
                for (child in snapshot.children) {
                    val cert = child.getValue(Certificate::class.java)
                    cert?.let { certificateList.add(it) }
                }
                certAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteCertificate(cert: Certificate) {
        val certId = cert.id ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Certificate")
            .setMessage("Are you sure you want to remove this certificate?")
            .setPositiveButton("Delete") { _, _ ->
                FirebaseDatabase.getInstance().getReference("Certificates")
                    .child(certId).removeValue().addOnSuccessListener {
                        Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddDonationDialog() {
        val dialogBinding = DialogAddDonationBinding.inflate(layoutInflater)

        // Spinner Setup
        val categories = arrayOf("Food", "Clothes", "Education Goods", "Medicine", "Others")
        dialogBinding.spinnerDonationType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        // Date Picker
        dialogBinding.etDonationDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                dialogBinding.etDonationDate.setText("$d/${m+1}/$y")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time Picker
        dialogBinding.etDonationTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, min ->
                dialogBinding.etDonationTime.setText(String.format("%02d:%02d", h, min))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Post Event") { _, _ ->
                val type = dialogBinding.spinnerDonationType.selectedItem.toString()
                val desc = dialogBinding.etDonationDesc.text.toString()
                val addr = dialogBinding.etDonationAddress.text.toString()
                val date = dialogBinding.etDonationDate.text.toString()
                val time = dialogBinding.etDonationTime.text.toString()

                if (desc.isNotEmpty() && addr.isNotEmpty()) {
                    saveDonationEvent(type, desc, addr, date, time)
                } else {
                    Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveDonationEvent(type: String, desc: String, addr: String, date: String, time: String) {
        val db = FirebaseDatabase.getInstance().getReference("DonationEvents")
        val eventId = db.push().key ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val event = DonationEvent(eventId, uid, type, desc, addr, date, time, eventStatus = false)

        db.child(eventId).setValue(event).addOnSuccessListener {
            Toast.makeText(context, "Event Published Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}