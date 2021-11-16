package com.example.myapplication.mysql
import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException

data class Homeworks(val h_id: Int, val task: String, val task_date: String)

object MySQL {

    lateinit var conn: Connection

    @JvmStatic
    fun connection(address: String, database: String, user: String, password: String): Boolean {
        var connected = false

        try {

            conn = DriverManager.getConnection("jdbc:mysql://$address/$database?user=$user&password=$password&useUnicode=true&characterEncoding=UTF-8")
            connected = true

        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return connected
    }

    fun fetchHomeworkTable(statement: String): MutableList<Homeworks> {
        val query = conn.prepareStatement(statement)
        val result = query.executeQuery()
        val has = mutableListOf<Homeworks>()
        try {
            while (result.next()) {
                val id = result.getInt("ha_id")
                val ha = result.getString("ha")
                val date = result.getString("datum")
                has.add(Homeworks(id, ha, date))
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return has
    }
}