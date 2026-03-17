package com.example.ngo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.widget.ImageView
import android.widget.TextView
class EducationActivity : AppCompatActivity() {

    lateinit var titleTextView: TextView
    lateinit var line1TextView: TextView
    lateinit var line2TextView: TextView
    lateinit var line3TextView: TextView
    lateinit var line4TextView: TextView
    lateinit var line5TextView: TextView
    lateinit var educationImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_education)

        titleTextView = findViewById(R.id.titleTextView)
        line1TextView = findViewById(R.id.line1TextView)
        line2TextView = findViewById(R.id.line2TextView)
        line3TextView = findViewById(R.id.line3TextView)
        line4TextView = findViewById(R.id.line4TextView)
        line5TextView = findViewById(R.id.line5TextView)
        educationImageView = findViewById(R.id.educationImageView)


        titleTextView.text = "📚 EDUCATION PROGRAM"

    }
}
