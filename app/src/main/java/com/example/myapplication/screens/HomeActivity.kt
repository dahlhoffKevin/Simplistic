package com.example.myapplication.screens

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class HomeActivity : AppCompatActivity() {

    private val toastindexcards = "Wechsle zu Karteikarten..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnIndexCards = findViewById<Button>(R.id.btn_indexcards)

        btnIndexCards.setOnClickListener {
            Toast.makeText(this, toastindexcards , Toast.LENGTH_LONG).show()

        }
    }
}