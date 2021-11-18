package com.example.myapplication.screens

import android.content.Intent
import android.media.metrics.Event
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class HomeworksActivity : AppCompatActivity(){

    private val toastindexcards = "Wechsle zu Karteikarten..."
    private val toastevents = "Wechsle zu Terminen..."
    private val toasthome = "Wechsle zu Startseite..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        val btnIndexCardsActivity: Button = findViewById(R.id.btn_homeworks_indexcards)
        val btnEventsActivity: Button = findViewById(R.id.btn_homeworks_events)
        val btnHomeActivity: Button = findViewById(R.id.btn_homeworks_home)

        btnHomeActivity.setOnClickListener{
            Toast.makeText(this, toasthome, Toast.LENGTH_SHORT).show()
            val switchH1 = Intent(this, HomeActivity::class.java)
            startActivity(switchH1)
        }
        btnEventsActivity.setOnClickListener{
            Toast.makeText(this, toastevents, Toast.LENGTH_SHORT).show()
            val switchE1 = Intent(this, EventsActivity::class.java)
            startActivity(switchE1)
        }
        btnIndexCardsActivity.setOnClickListener{
            Toast.makeText(this, toastindexcards, Toast.LENGTH_SHORT).show()
            val switchIC2 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC2)
        }
    }
}