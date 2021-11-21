package com.example.myapplication.mysql
import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException

data class Homeworks(val h_id: Int, val task: String, val task_date: String)

object MySQL {

    lateinit var conn: Connection
    private val has = mutableListOf<Homeworks>()

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

    fun prettyPrint(): String {

        var indentLevel = 0
        val indentWidth = 0

        fun padding() = "".padStart(indentLevel * indentWidth)

        val toString = has.toString()

        val stringBuilder = StringBuilder(toString.length)

        var i = 0
        while (i < toString.length) {
            when (val char = toString[i]) {
                '(', '[', '{' -> {
                    indentLevel++
                    stringBuilder.appendLine(char).append(padding())
                }
                ')', ']', '}' -> {
                    indentLevel--
                    stringBuilder.appendLine().append(padding()).append(char)
                }
                ',' -> {
                    stringBuilder.appendLine(char).append(padding())
                    // ignore space after comma as we have added a newline
                    val nextChar = toString.getOrElse(i + 1) { char }
                    if (nextChar == ' ') i++
                }
                else -> {
                    stringBuilder.append(char)
                }
            }
            i++
        }
        return stringBuilder.toString().replace(Regex("""[(,)]"""), "")
            .replace("[","")
            .replace("]","")
            .replace("h_id=", "")
            .replace("task=", "")
            .replace("task_date=", "")
    }
}