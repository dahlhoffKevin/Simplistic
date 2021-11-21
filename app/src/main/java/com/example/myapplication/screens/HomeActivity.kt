package com.example.myapplication.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnIndexCardsActivity: Button = findViewById(R.id.btn_home_indexcards)
        val btnEventsActivity: Button = findViewById(R.id.btn_home_events)
        val btnHomeworksActivity: Button = findViewById(R.id.btn_home_homeworks)

        btnIndexCardsActivity.setOnClickListener {
            val switchIC0 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC0)
        }
        btnEventsActivity.setOnClickListener{
            val intent1 = Intent(this, EventsActivity::class.java)
            startActivity(intent1)
        }
        btnHomeworksActivity.setOnClickListener{
            val intent2 = Intent(this, HomeworksActivity::class.java)
            startActivity(intent2)
        }
    }
}