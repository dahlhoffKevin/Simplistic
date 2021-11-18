package com.example.myapplication.screens

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class EventsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val btnIndexCardsActivity = findViewById<Button>(R.id.btn_home_indexcards)
        val btnHomeworksActivity = findViewById<Button>(R.id.btn_home_homeworks)

    }
}