package com.example.myapplication.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class EventsActivity : AppCompatActivity(){

    private val toastindexcards = "Wechsle zu Karteikarten..."
    private val toasthomeworks = "Wechsle zu Hausaufgaben..."
    private val toasthome = "Wechsle zu Startseite..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val btnIndexCardsActivity = findViewById<Button>(R.id.btn_events_indexcards)
        val btnHomeworksActivity = findViewById<Button>(R.id.btn_events_homeworks)
        val btnHome = findViewById<Button>(R.id.btn_events_home)

        btnHome.setOnClickListener{
            Toast.makeText(this, toasthome, Toast.LENGTH_SHORT).show()
            val switchH0 = Intent(this, HomeActivity::class.java)
            startActivity(switchH0)
        }
        btnIndexCardsActivity.setOnClickListener{
            Toast.makeText(this, toastindexcards, Toast.LENGTH_SHORT).show()
            val switchIC1 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC1)
        }
        btnHomeworksActivity.setOnClickListener{
            Toast.makeText(this, toasthomeworks, Toast.LENGTH_SHORT).show()
            val switchHW1 = Intent(this, HomeworksActivity::class.java)
            startActivity(switchHW1)
        }
    }
}