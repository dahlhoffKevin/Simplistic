package com.example.myapplication.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R


class HomeActivity : AppCompatActivity() {

    private val toastindexcards = "Wechsle zu Karteikarten..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnIndexCardsActivity = findViewById<Button>(R.id.btn_indexcards)
        val btnEventsActivity = findViewById<Button>(R.id.btn_events)
        val btnHomeworksActivity = findViewById<Button>(R.id.btn_homeworks)

        btnIndexCardsActivity.setOnClickListener {

            val intent = Intent(this, IndexCardsActivity::class.java)
            startActivity(intent)
        }
    }
}