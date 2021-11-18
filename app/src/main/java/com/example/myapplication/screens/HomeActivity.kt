package com.example.myapplication.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R


class HomeActivity : AppCompatActivity() {

    private val toastindexcards = "Wechsle zu Karteikarten..."
    private val toastevents = "Wechsle zu Terminen..."
    private val toasthomeworks = "Wechsle zu Hausaufgaben..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnIndexCardsActivity: Button = findViewById(R.id.btn_home_indexcards)
        val btnEventsActivity: Button = findViewById(R.id.btn_home_events)
        val btnHomeworksActivity: Button = findViewById(R.id.btn_home_homeworks)

        btnIndexCardsActivity.setOnClickListener {
            Toast.makeText(this, toastindexcards, Toast.LENGTH_SHORT).show()
            val switchIC0 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC0)
        }
        btnEventsActivity.setOnClickListener{
            Toast.makeText(this, toastevents, Toast.LENGTH_SHORT).show()
            val intent1 = Intent(this, EventsActivity::class.java)
            startActivity(intent1)
        }
        btnHomeworksActivity.setOnClickListener{
            Toast.makeText(this, toasthomeworks, Toast.LENGTH_SHORT).show()
            val intent2 = Intent(this, HomeworksActivity::class.java)
            startActivity(intent2)
        }
    }
}