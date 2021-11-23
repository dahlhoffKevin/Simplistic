package com.example.myapplication.mysql
import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException

data class Homeworks(val task: String, val task_date: String)
data class Classtest(val test: String, val test_date: String)
data class News(val news: String, val news_date: String)

object MySQL {
    lateinit var conn: Connection
    private val has = mutableListOf<Homeworks>()
    private val clas = mutableListOf<Classtest>()
    private val news = mutableListOf<News>()

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

    fun fetchHomeworkTable(): MutableList<Homeworks> {
        val query = conn.prepareStatement("SELECT task,task_date FROM homework LIMIT 5")
        val result = query.executeQuery()
        try {
            while (result.next()) {
                val task = result.getString("task")
                val taskDate = result.getString("task_date")
                has.add(Homeworks(task, taskDate))
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return has
    }

    fun homeworksPrettyPrint(): String {
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
            .replace("task=", "Aufgabe: ")
            .replace("task_date=", "Abgabe: ")
            .replace("Homeworks", "")
    }

    fun fetchEventsTable(): MutableList<Classtest> {
        val query = conn.prepareStatement("SELECT test,test_date FROM classtest LIMIT 5")
        val result = query.executeQuery()
        try {
            while (result.next()) {
                val test = result.getString("test")
                val testDate = result.getString("test_date")
                clas.add(Classtest(test, testDate))
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return clas
    }

    fun eventsPrettyPrint(): String {
        var indentLevel = 0
        val indentWidth = 0
        fun padding() = "".padStart(indentLevel * indentWidth)
        val toString = clas.toString()
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
            .replace("test=", "Klausur/Test: ")
            .replace("test_date=", "Datum: ")
            .replace("Classtest", "")
    }

    fun fetchNewsTable(): MutableList<News> {
        val query = conn.prepareStatement("SELECT news,news_date FROM news LIMIT 1")
        val result = query.executeQuery()
        try {
            while (result.next()) {
                val test = result.getString("news")
                val testDate = result.getString("news_date")
                news.add(News(test, testDate))
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return news
    }

    fun newsPrettyPrint(): String {
        var indentLevel = 0
        val indentWidth = 0
        fun padding() = "".padStart(indentLevel * indentWidth)
        val toString = news.toString()
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
            .replace("news=", "")
            .replace("news_date=", "Datum: ")
            .replace("News", "")
    }

}