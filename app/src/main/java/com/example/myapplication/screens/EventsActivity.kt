package com.example.myapplication.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class EventsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val btnIndexCardsActivity = findViewById<Button>(R.id.btn_events_indexcards)
        val btnHomeworksActivity = findViewById<Button>(R.id.btn_events_homeworks)
        val btnHome = findViewById<Button>(R.id.btn_events_home)

        btnHome.setOnClickListener{
            val switchH0 = Intent(this, HomeActivity::class.java)
            startActivity(switchH0)
        }
        btnIndexCardsActivity.setOnClickListener{
            val switchIC1 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC1)
        }
        btnHomeworksActivity.setOnClickListener{
            val switchHW1 = Intent(this, HomeworksActivity::class.java)
            startActivity(switchHW1)
        }
    }
}