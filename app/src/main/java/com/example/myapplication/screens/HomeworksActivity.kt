package com.example.myapplication.screens

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class HomeworksActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        val btnIndexCardsActivity = findViewById<Button>(R.id.btn_indexcards)
        val btnEventsActivity = findViewById<Button>(R.id.btn_events)

    }
}