package com.example.ngo.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ngo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
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


        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        auth = FirebaseAuth.getInstance()

        // If user is already logged in, skip to Main
        if (auth.currentUser != null) {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        // You could add a progress bar here for better UX
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else {

                    val errorMessage = task.exception?.message ?: "An error occurred"

                    // Logic to catch "malformed" or "invalid" technical errors
                    if (errorMessage.contains("malformed", ignoreCase = true) ||
                        errorMessage.contains("invalid", ignoreCase = true)) {

                        Toast.makeText(this, "Please enter a valid email and password", Toast.LENGTH_LONG).show()
                    } else {
                        // Fallback for other errors (like network issues)
                        Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}
