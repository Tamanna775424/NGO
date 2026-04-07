package com.example.ngo.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ngo.Activities.LoginActivity
import com.example.ngo.R
import com.example.ngo.Utils.User
import com.example.ngo.databinding.DialogEditProfileBinding
import com.example.ngo.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Firebase
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            fetchUserData()
        } else {
            redirectToLogin()
        }

        // 2. Edit Profile Click
        binding.btnEditProfile.setOnClickListener {
            showEditDialog()
        }
        // 3. Logout Logic
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            redirectToLogin()
        }


        view.findViewById<Button>(R.id.btnevent).setOnClickListener {
            findNavController().navigate(R.id.adminViewEventsListFragment)

        }

    }

    private fun fetchUserData() {
        // Use ValueEventListener for real-time updates
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)

                    user?.let {
                        // Set Text Details
                        binding.tvProfileName.text = it.name ?: "N/A"
<<<<<<< HEAD
                        binding.tvProfileEmail.text = FirebaseAuth.getInstance().currentUser?.email.toString() ?: "No Email"
=======
                        binding.tvProfileEmail.text = FirebaseAuth.getInstance().currentUser?.email.toString()
>>>>>>> ce1adfdd9eb3a055b2c0955ea4622f5f42fa2867
                        binding.tvProfilePhone.text = it.phone ?: "No Phone"

                        // Load Profile Image using Glide
                        if (isAdded && it.profileUrl != null) {
//                            Glide.with(this@ProfileFragment)
//                                .load(it.profileUrl)
//                                .placeholder(android.R.drawable.ic_menu_gallery)
//                                .error(android.R.drawable.ic_menu_report_image)
//                                .into(binding.ivProfileDetail)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun showEditDialog() {
        // 1. Inflate the custom layout using ViewBinding
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)

        // 2. Pre-fill with current data
        dialogBinding.etEditName.setText(binding.tvProfileName.text)
        dialogBinding.etEditPhone.setText(binding.tvProfilePhone.text)

        // 3. Build the Material Dialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Update") { dialog, _ ->
                val newName = dialogBinding.etEditName.text.toString().trim()
                val newPhone = dialogBinding.etEditPhone.text.toString().trim()

                if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                    updateProfile(newName, newPhone)
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateProfile(name: String, phone: String) {
        val updates = mapOf(
            "name" to name,
            "phone" to phone
        )

        dbRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Prevent memory leaks
        _binding = null
    }
}



