package com.example.ngo.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ngo.Utils.User
import com.example.ngo.AppwriteManager
import com.example.ngo.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.ivProfile.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            if (imeVisible) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, imeHeight)
            }

            insets
        }
        auth = FirebaseAuth.getInstance()

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.ivProfile.setOnClickListener { pickImage.launch("image/*") }

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val pass = binding.etPassword.text.toString().trim()

                registerUser(name, phone, email, pass)
            }
        }
    }

    /**
     * Logical Checks for user inputs
     */
    private fun validateInputs(): Boolean {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        return when {

            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                false
            }
            phone.length < 10 -> {
                binding.etPhone.error = "Enter a valid phone number"
                false
            }
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Enter a valid email address"
                false
            }
            pass.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                false
            }
            else -> true
        }
    }

    private fun registerUser(name: String, phone: String, email: String, pass: String) {
        setLoadingState(true)

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                uploadImageAndSaveData(userId, name, phone, email)
            } else {
                setLoadingState(false)
                handleAuthError(task.exception?.message ?: "Authentication failed")
            }
        }
    }

    private fun uploadImageAndSaveData(uid: String, name: String, phone: String, email: String) {
        lifecycleScope.launch {
            try {
                // 1. Upload to Appwrite
//                val imageUrl = AppwriteManager.getInstance(this@RegisterActivity)
//                    .uploadImageFromUri(imageUri!!)

                // 2. Create User Object using Data Class
                val user = User(
                    uid = uid,
                    name = name,
                    phone = phone,
                    email = email,
                    profileUrl = ""
                )

                // 3. Save to Firebase Realtime Database
                FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    .setValue(user)
                    .addOnSuccessListener {
                        setLoadingState(false)
                        showToast("Account Created Successfully!")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        setLoadingState(false)
                        showToast("Database Error: ${e.message}")
                    }

            } catch (e: Exception) {
                setLoadingState(false)
                showToast("Upload Failed: ${e.message}")
            }
        }
    }

    private fun handleAuthError(msg: String) {
        if (msg.contains("malformed", true) || msg.contains("invalid", true)) {
            showToast("Please enter a valid email and password")
        } else {
            showToast("Error: $msg")
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}