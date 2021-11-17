package com.example.myapplication.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.R
import java.util.*


class IndexCards : AppCompatActivity() {
    private lateinit var edTopic: EditText
    private lateinit var edContnet: EditText
    private lateinit var btnAdd: Button
    private lateinit var btwView: Button
    private lateinit var btnUpdate: Button

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: StudentAdapter? = null
    private var std:ContentModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indexcards_0)

        val btnEventsActivity = findViewById<Button>(R.id.btn_events)
        val btnHomeworksActivity = findViewById<Button>(R.id.btn_homeworks)

        sqLiteHelper = SQLiteHelper(this)

        initView()
        initRecyclerVew()

        btnAdd.setOnClickListener{ addStudent() }
        btwView.setOnClickListener{ getStudents() }
        btnUpdate.setOnClickListener{ updateStudent() }

        adapter?.setOnClickitem {
            Toast.makeText(this, it.topic, Toast.LENGTH_SHORT).show()
            edTopic.setText(it.topic)
            edContnet.setText(it.content)
            std = it
        }
        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }
    }

    private fun getStudents(){
        val stdList = sqLiteHelper.getAllStudent()
        Log.e("Log", "${stdList.size}")
        adapter?.addItems(stdList)
    }

    private fun addStudent(){
        val name = edTopic.text.toString()
        val email = edContnet.text.toString()

        if(name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter the required data", Toast.LENGTH_SHORT).show()
        } else {
            val std = ContentModel(topic = name, content = email)
            val status = sqLiteHelper.insertStudent(std)
            // Check insert success or not success
            if(status > -1) {
                Toast.makeText(this, "Student Added...", Toast.LENGTH_SHORT).show()
                clearEditText()
                getStudents()
            } else {
                Toast.makeText(this, "Record not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStudent(){
        val name = edTopic.text.toString()
        val email = edContnet.text.toString()

        // Check record, not change
        if(name == std?.topic && email == std?.content){
            Toast.makeText(this, "Record not changed...", Toast.LENGTH_SHORT).show()
            return
        }
        if(std == null) return
        val std = ContentModel(id = std!!.id, topic = name, content = email)
        val status = sqLiteHelper.updateStudent(std)

        if(status >- 1){
            clearEditText()
            getStudents()
        } else{
            Toast.makeText(this, "Update failed...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id: Int){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete item?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes"){ dialog, _ ->
            sqLiteHelper.deleteStudentById(id)
            getStudents()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun clearEditText() {
        edTopic.setText("")
        edContnet.setText("")
        edTopic.requestFocus()
    }

    private fun initRecyclerVew(){
        recyclerView.layoutManager  = LinearLayoutManager(this)
        adapter                     = StudentAdapter()
        recyclerView.adapter        = adapter
    }

    private fun initView() {
        edTopic          = findViewById(R.id.edName)
        edContnet         = findViewById(R.id.edEmail)
        btnAdd          = findViewById(R.id.btnAdd)
        btwView         = findViewById(R.id.btnView)
        btnUpdate       = findViewById(R.id.btnUpdate)
        recyclerView    = findViewById(R.id.recyclerView)
    }
}



class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION  = 1
        private const val DATABASE_NAME     = "student.db"
        private const val TBL_DATA          = "tbl_data"
        private const val ID                = "id"
        private const val TOPIC             = "topic"
        private const val CONTENT           = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = (
                "CREATE TABLE " + TBL_DATA +
                        "(" + ID + " INTEGER PRIMARY KEY, " + TOPIC + " TEXT," + CONTENT + " TEXT" + ")")
        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("""DROP TABLE IF EXISTS $TBL_DATA""".trimIndent())
        onCreate(db)
    }

    fun insertStudent(std: ContentModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(TOPIC, std.topic)
        contentValues.put(CONTENT, std.content)

        val success = db.insert(TBL_DATA, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getAllStudent(): ArrayList<ContentModel> {
        val stdList : ArrayList<ContentModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_DATA"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var topic: String
        var content: String

        if(cursor.moveToFirst()){
            do {
                id      = cursor.getInt(cursor.getColumnIndex("id"))
                topic    = cursor.getString(cursor.getColumnIndex("name"))
                content   = cursor.getString(cursor.getColumnIndex("email"))

                val std = ContentModel(id = id, topic = topic, content = content)
                stdList.add(std)
            } while (cursor.moveToNext())
        }

        return stdList
    }

    fun updateStudent(std: ContentModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(TOPIC, std.topic)
        contentValues.put(CONTENT, std.content)
        val success = db.update(TBL_DATA, contentValues, "id=" + std.id, null)
        db.close()
        return success
    }

    fun deleteStudentById(id: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_DATA, "id=$id", null)
        db.close()
        return success
    }
}


class StudentAdapter: RecyclerView.Adapter<StudentAdapter.ContentViewHolder>() {
    private var stdList: ArrayList<ContentModel> = ArrayList()
    private var onClickItem: ((ContentModel) -> Unit)? = null
    private var onClickDeleteItem: ((ContentModel) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<ContentModel>) {
        this.stdList = items
        notifyDataSetChanged()
    }

    fun setOnClickitem(callback: (ContentModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (ContentModel) -> Unit) {
        this.onClickDeleteItem = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.activity_indexcards_1, parent, false)
    )

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val std = stdList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(std)
            holder.btnDelete.setOnClickListener { onClickDeleteItem?.invoke(std) }
        }
    }

    override fun getItemCount(): Int {
        return stdList.size
    }

    class ContentViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var id = view.findViewById<TextView>(R.id.tvId)
        private var topic = view.findViewById<TextView>(R.id.tvTopic)
        private var content = view.findViewById<TextView>(R.id.tvContent)
        var btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bindView(std: ContentModel) {
            id.text = std.id.toString()
            topic.text = std.topic
            content.text = std.content
        }
    }
}

data class ContentModel(var id: Int = getAutoId(), var topic: String = "", var content: String = "") {
    companion object {
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }
}