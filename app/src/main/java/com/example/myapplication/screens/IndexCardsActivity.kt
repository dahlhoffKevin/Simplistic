package com.example.myapplication.screens


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import java.util.*
import kotlin.collections.ArrayList


class IndexCardsActivity : AppCompatActivity() {
    private lateinit var edTopic: EditText
    private lateinit var edContent: EditText
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

        initView()
        initRecyclerVew()
        sqLiteHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener{ addContent() }
        btwView.setOnClickListener{ getContent() }
        btnUpdate.setOnClickListener{ updateContent() }

        adapter?.setOnClickitem {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            edTopic.setText(it.name)
            edContent.setText(it.email)
            std = it
        }
        adapter?.setOnClickDeleteItem {
            deleteContent(it.id)
        }
    }

    private fun getContent(){
        val stdList = sqLiteHelper.getAllContent()
        Log.e("Log", "${stdList.size}")
        adapter?.addItems(stdList)
    }

    private fun addContent(){
        val topic = edTopic.text.toString()
        val content = edContent.text.toString()

        val requiereddata = "Bitte erforderliche Felder ausfüllen"
        val contentadded = "Inhalt hinzugefügt..."
        val notsaved = "Inhalt nicht hinzugefügt..."

        if(topic.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, requiereddata, Toast.LENGTH_SHORT).show()
        } else {
            val std = ContentModel(name = topic, email = content)
            val status = sqLiteHelper.insertStudent(std)
            // Check insert success or not success
            if(status > -1) {
                Toast.makeText(this, contentadded, Toast.LENGTH_SHORT).show()
                clearEditText()
                getContent()
            } else {
                Toast.makeText(this, notsaved, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateContent(){
        val name = edTopic.text.toString()
        val email = edContent.text.toString()

        val notchanged = "Inhalt nicht aktualisiert!"
        val updatefail = "Aktualisieren fehlgeschlagen!"

        // Check record, not change
        if(name == std?.name && email == std?.email){
            Toast.makeText(this, notchanged, Toast.LENGTH_SHORT).show()
            return
        }
        if(std == null) return
        val std = ContentModel(id = std!!.id, name = name, email = email)
        val status = sqLiteHelper.updateContent(std)

        if(status >- 1){
            clearEditText()
            getContent()
        } else{
            Toast.makeText(this, updatefail, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContent(id: Int){
        val builder = AlertDialog.Builder(this)
        val deleteitem = "Möchtest du die Karteikarte wirklich löschen?"

        builder.setMessage(deleteitem)
        builder.setCancelable(true)

        builder.setPositiveButton("Ja"){ dialog, _ ->
            sqLiteHelper.deleteStudentById(id)
            getContent()
            dialog.dismiss()
        }
        builder.setNegativeButton("Nein") { dialog, _ ->
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun clearEditText() {
        edTopic.setText("")
        edContent.setText("")
        edTopic.requestFocus()
    }

    private fun initRecyclerVew(){
        recyclerView.layoutManager  = LinearLayoutManager(this)
        adapter                     = StudentAdapter()
        recyclerView.adapter        = adapter
    }

    private fun initView() {
        edTopic         = findViewById(R.id.edName)
        edContent       = findViewById(R.id.edEmail)
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

    fun insertStudent(std: ContentModel): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
        contentValues.put(EMAIL, std.email)

        val success = db.insert(TBL_STUDENT, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getAllContent(): ArrayList<ContentModel> {
        val stdList : ArrayList<ContentModel> = ArrayList()
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

                val std = ContentModel(id = id, name = name, email = email)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        return stdList
    }

    fun updateContent(std: ContentModel): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, std.id)
        contentValues.put(NAME, std.name)
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
    private var stdList: ArrayList<ContentModel> = ArrayList()
    private var onClickItem:((ContentModel)->Unit)?= null
    private var onClickDeleteItem:((ContentModel)->Unit)?= null

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<ContentModel>){
        this.stdList = items
        notifyDataSetChanged()
    }

    fun setOnClickitem(callback: (ContentModel)->Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (ContentModel) -> Unit){
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
        private var name = view.findViewById<TextView>(R.id.tvTopic)
        private var email = view.findViewById<TextView>(R.id.tvContent)
        var btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bindView(std: ContentModel) {
            id.text = std.id.toString()
            name.text = std.name
            email.text = std.email
        }
    }
}

data class ContentModel(var id: Int = getAutoId(), var name: String = "", var email: String = "") {
    companion object {
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }
}