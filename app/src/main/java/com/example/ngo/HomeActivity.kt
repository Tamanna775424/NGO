package com.example.ngo

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    lateinit var foodBtn: Button
    lateinit var eduBtn: Button
    lateinit var healthBtn: Button
    lateinit var animalBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        foodBtn = findViewById(R.id.foodBtn)
        eduBtn = findViewById(R.id.eduBtn)
        healthBtn = findViewById(R.id.healthBtn)
        animalBtn = findViewById(R.id.animalBtn)


        foodBtn.setOnClickListener {
            Toast.makeText(this, "Food Donation Selected", Toast.LENGTH_SHORT).show()
        }

        eduBtn.setOnClickListener {
            Toast.makeText(this, "Education Campaigns", Toast.LENGTH_SHORT).show()
        }

        healthBtn.setOnClickListener {
            Toast.makeText(this, "Health Support", Toast.LENGTH_SHORT).show()
        }

        animalBtn.setOnClickListener {
            Toast.makeText(this, "Animal Welfare", Toast.LENGTH_SHORT).show()
        }
    }
}