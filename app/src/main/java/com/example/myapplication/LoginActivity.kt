package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.mysql.MySQL
import com.example.myapplication.screens.HomeActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // loads data which was typed in by the user
        loadData()

        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            login()
        }
    }

    // is making a toast
    private fun makeToast(toast : String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    // saves data typed in by the user
    private fun saveData() {
        // when the checkbox is checked then save the user data
        val checkBoxSaveData = findViewById<CheckBox>(R.id.checkBoxSaveData)
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (checkBoxSaveData.isChecked) {
            val ip = findViewById<EditText>(R.id.editTextIpAddress).text.toString()
            val user = findViewById<EditText>(R.id.editTextUsername).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val database = findViewById<EditText>(R.id.editTextDatabase).text.toString()

            editor.apply {
                putString("STRING_KEY_IP", ip)
                putString("STRING_KEY_USER", user)
                putString("STRING_KEY_PASSWORD", password)
                putString("STRING_KEY_DATABASE", database)
                putBoolean("BOOLEAN_KEY_CHECK_BOX", checkBoxSaveData.isChecked)
            }.apply()

        } else {
            editor.apply {
                putString("STRING_KEY_IP", "")
                putString("STRING_KEY_USER", "")
                putString("STRING_KEY_PASSWORD", "")
                putString("STRING_KEY_DATABASE", "")
                putBoolean("BOOLEAN_KEY_CHECK_BOX", checkBoxSaveData.isChecked)
            }.apply()
        }
    }

    // loads data typed in by the user
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedStringIp = sharedPreferences.getString("STRING_KEY_IP", null)
        val savedStringUser = sharedPreferences.getString("STRING_KEY_USER", null)
        val savedStringPassword = sharedPreferences.getString("STRING_KEY_PASSWORD", null)
        val savedStringDatabase = sharedPreferences.getString("STRING_KEY_DATABASE", null)
        val savedBooleanCheckBox = sharedPreferences.getBoolean("BOOLEAN_KEY_CHECK_BOX", true)

        findViewById<EditText>(R.id.editTextIpAddress).setText(savedStringIp)
        findViewById<EditText>(R.id.editTextUsername).setText(savedStringUser)
        findViewById<EditText>(R.id.editTextPassword).setText(savedStringPassword)
        findViewById<EditText>(R.id.editTextDatabase).setText(savedStringDatabase)
        findViewById<CheckBox>(R.id.checkBoxSaveData).isChecked = savedBooleanCheckBox
    }

    // runs when the button was clicked
    @RequiresApi(Build.VERSION_CODES.M)
    private fun login() {
        var argsFilled = false
        var connectedToInternet = true

        val intent = Intent(this, HomeActivity::class.java)

        val toast1 = "Du bist nicht mit dem Internet verbunden"
        val toast2 = "Mit Server verbunden"
        val toast3 = "Konnte keine Verbindung zum Server herstellen"
        val toast4 = "Alle wichtigen Felder müssen ausgefüllt sein"

        // GlobalScope (Async Task) for checking internet connection
        GlobalScope.launch {
            Looper.prepare()
            if (! isOnline(this@LoginActivity)) {
                makeToast(toast1)
                connectedToInternet = false
            }
            Looper.loop()
        }

        try {
            // if the phone is connected to internet then ...
            if (connectedToInternet) {
                val ip = findViewById<EditText>(R.id.editTextIpAddress).text.toString()
                val user = findViewById<EditText>(R.id.editTextUsername).text.toString()
                val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
                val database = findViewById<EditText>(R.id.editTextDatabase).text.toString()

                if (ip != "" || user != "") {
                    argsFilled = true
                }

                // if all necessary fields are filled in, then ...
                if (argsFilled) {
                    // GlobalScope (Async Task) for connecting to SQL-Database
                    try {
                        GlobalScope.launch {
                            Looper.prepare()
                            // checks if a database connection can be established
                            if (MySQL.connection(ip, database, user, password)) {
                                MySQL.fetchHomeworkTable()
                                MySQL.fetchEventsTable()
                                MySQL.fetchNewsTable()
                                startActivity(intent)
                                makeToast(toast2)
                            } else {
                                makeToast(toast3)
                            }
                            Looper.loop()
                        }
                    } catch (e: Exception) {
                        makeToast(e.toString())
                    }
                } else {
                    makeToast(toast4)
                }
            }
            /**
             * TODO: Wenn keine Internetverbindung vorhanden ist, sollen bestehende Information aus der Datenbank geladen werden
             */
            saveData()
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // checking internet connection
    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities        = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}