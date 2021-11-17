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
    private var std:StudentModel? = null


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
            edContnet.setText(it.email)
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
            val std = StudentModel(topic = name, email = email)
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
        if(name == std?.topic && email == std?.email){
            Toast.makeText(this, "Record not changed...", Toast.LENGTH_SHORT).show()
            return
        }
        if(std == null) return
        val std = StudentModel(id = std!!.id, topic = name, email = email)
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
        private const val TBL_STUDENT       = "tbl_student"
        private const val ID                = "id"
        private const val NAME              = "name"
        private const val EMAIL             = "email"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = (
                "CREATE TABLE " + TBL_STUDENT +
                        "(" + ID + " INTEGER PRIMARY KEY, " + NAME + " TEXT," + EMAIL + " TEXT" + ")")

        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_STUDENT")
        onCreate(db)
    }

    fun insertStudent(std: StudentModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.topic)
        contentValues.put(EMAIL, std.email)

        val success = db.insert(TBL_STUDENT, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getAllStudent(): ArrayList<StudentModel> {
        val stdList : ArrayList<StudentModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_STUDENT"
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
        var name: String
        var email: String

        if(cursor.moveToFirst()){
            do {
                id      = cursor.getInt(cursor.getColumnIndex("id"))
                name    = cursor.getString(cursor.getColumnIndex("name"))
                email   = cursor.getString(cursor.getColumnIndex("email"))

                val std = StudentModel(id = id, topic = name, email = email)
                stdList.add(std)
            } while (cursor.moveToNext())
        }

        return stdList
    }

    fun updateStudent(std: StudentModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.topic)
        contentValues.put(EMAIL, std.email)
        val success = db.update(TBL_STUDENT, contentValues, "id=" + std.id, null)
        db.close()
        return success
    }

    fun deleteStudentById(id: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_STUDENT, "id=$id", null)
        db.close()
        return success
    }
}


class StudentAdapter: RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private var stdList: ArrayList<StudentModel> = ArrayList()
    private var onClickItem:((StudentModel)->Unit)?= null
    private var onClickDeleteItem:((StudentModel)->Unit)?= null


    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<StudentModel>){
        this.stdList = items
        notifyDataSetChanged()
    }

    fun setOnClickitem(callback: (StudentModel)->Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (StudentModel) -> Unit){
        this.onClickDeleteItem = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.activity_indexcards_1, parent, false)
    )

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val std = stdList[position]
        holder.bindView(std)
        holder.itemView.setOnClickListener{ onClickItem?.invoke(std)
            holder.btnDelete.setOnClickListener{ onClickDeleteItem?.invoke(std) }
        }
    }

    override fun getItemCount(): Int {
        return stdList.size

    }

    class StudentViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        private var id = view.findViewById<TextView>(R.id.tvId)
        private var name = view.findViewById<TextView>(R.id.tvName)
        private var email = view.findViewById<TextView>(R.id.tvEmail)
        var btnDelete: Button = view.findViewById<Button>(R.id.btnDelete)

        fun bindView(std: StudentModel) {
            id.text = std.id.toString()
            name.text = std.topic
            email.text = std.email
        }
    }
}


data class StudentModel(var id: Int = getAutoId(), var topic: String = "", var email: String = "") {

    companion object {
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }
}