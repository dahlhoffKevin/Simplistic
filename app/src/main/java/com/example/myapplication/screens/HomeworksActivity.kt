package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.mysql.MySQL
import com.example.myapplication.mysql.MySQL.prettyPrint

class HomeworksActivity : AppCompatActivity(){

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeworks)

        val btnIndexCardsActivity: Button = findViewById(R.id.btn_homeworks_indexcards)
        val btnEventsActivity: Button = findViewById(R.id.btn_homeworks_events)
        val btnHomeActivity: Button = findViewById(R.id.btn_homeworks_home)

        btnHomeActivity.setOnClickListener{
            val switchH1 = Intent(this, HomeActivity::class.java)
            startActivity(switchH1)
        }
        btnEventsActivity.setOnClickListener{
            val switchE1 = Intent(this, EventsActivity::class.java)
            startActivity(switchE1)
        }
        btnIndexCardsActivity.setOnClickListener{
            val switchIC2 = Intent(this, IndexCardsActivity::class.java)
            startActivity(switchIC2)
        }

        val hwTextView = findViewById<TextView>(R.id.hw_01)
        hwTextView.text = prettyPrint()

    }
}