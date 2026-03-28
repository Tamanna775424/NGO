package com.example.ngo.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ngo.Fragments.PaymentFragment
import com.example.ngo.R
import com.example.ngo.databinding.ActivityBottomNavigationBinding
import com.razorpay.PaymentResultListener

class BottomNavigationActivity : AppCompatActivity(),PaymentResultListener {

    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0)
            insets
        }


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
        navView.setupWithNavController(navController)
    }


    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        // Find the Fragment currently attached
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

        // Forward the result to the fragment if it implements the listener
        if (currentFragment is PaymentFragment) {
            currentFragment.onPaymentSuccess(razorpayPaymentId)
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

        if (currentFragment is PaymentFragment) {
            currentFragment.onPaymentError(code, response)
        }
    }
}