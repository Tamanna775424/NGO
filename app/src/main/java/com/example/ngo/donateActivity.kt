package com.example.ngo

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FoodDonationActivity : AppCompatActivity() {

    lateinit var titleTextView: TextView
    lateinit var line1TextView: TextView
    lateinit var line2TextView: TextView
    lateinit var line3TextView: TextView
    lateinit var donationImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_food_donation)


        titleTextView = findViewById(R.id.titleTextView)
        line1TextView = findViewById(R.id.line1TextView)
        line2TextView = findViewById(R.id.line2TextView)
        line3TextView = findViewById(R.id.line3TextView)
        donationImageView = findViewById(R.id.donationImageView)

        titleTextView.text = "🥗 FOOD DONATION PROGRAM"
    }
}