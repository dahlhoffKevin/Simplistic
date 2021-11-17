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


// TODO: Eventuell den Update Button entfernen, da er eventuell zu viel Verwirrung stiften könnte
// TODO: Die Karteikarten werden nur dann angezeigt, wenn sie hinzugefügt werden oder der Ansehen Button geklickt worden ist.
//       In späteren Versionen sollte dies verändert werden.


class IndexCardsActivity : AppCompatActivity() {
    private lateinit var edTopic: EditText
    private lateinit var edContent: EditText
    private lateinit var btnAdd: Button
    private lateinit var btwView: Button
    private lateinit var btnUpdate: Button

    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: IndexCardsAdapter? = null
    private var std:IndexCardsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_indecards_0)

        sqLiteHelper = SQLiteHelper(this)

        initView(); initRecyclerVew()

        btnAdd.setOnClickListener{addContent()}
        btwView.setOnClickListener{getContent()}
        btnUpdate.setOnClickListener{updateContent()}

        adapter?.setOnClickitem {
            Toast.makeText(this, it.topic, Toast.LENGTH_SHORT).show()
            edTopic.setText(it.topic); edContent.setText(it.content)
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
        val topic   = edTopic.text.toString()
        val content = edContent.text.toString()

        val enterdata       = "Bitte fülle die benötigten Felder aus"                               // "Please enter required data"
        val contentadded    = "Inhalt hinzugefügt..."                                               // "Content added"
        val errortoast      = "Es ist ein Fehler aufgetreten, Inhalt nicht hinzugefügt!"            // "Error occurred, content not added!"


        if(topic.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, enterdata , Toast.LENGTH_SHORT).show()
        } else {
            val std     = IndexCardsModel(topic = topic, content = content)
            val status  = sqLiteHelper.insertStudent(std)

            // Check insert success or not success
            if(status > -1) {
                Toast.makeText(this, contentadded, Toast.LENGTH_LONG).show()
                clearEditText(); getContent()
            } else {
                Toast.makeText(this, errortoast, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun updateContent(){
        val topic   = edTopic.text.toString()
        val content = edContent.text.toString()

        val errortoast0 = "Inhalt nicht aktualisiert!"                                              // "Content has not updated!"
        val errortoast1 = "Aktualisieren fehlgeschlagen!"                                           // "Update failed!"

        // Check record if did not changed
        if(topic == std?.topic && content == std?.content){
            Toast.makeText(this, errortoast0, Toast.LENGTH_LONG).show()
            return
        }
        if(std == null)
            return
            val std = IndexCardsModel(id = std!!.id, topic = topic, content = content)
            val status = sqLiteHelper.updateContent(std)

        if(status >- 1){
            clearEditText(); getContent()
        } else{
            Toast.makeText(this, errortoast1, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContent(id: Int){
        val builder     = AlertDialog.Builder(this)
        val removeitem  = "Möchtest du die Karteikarte wirklich löschen?"                            // "Are you sure you want to delete item?"

        builder.setMessage(removeitem); builder.setCancelable(true)

        builder.setPositiveButton("Ja"){ dialog, _ ->                                           // "Yes"
            sqLiteHelper.deleteContentById(id)
            getContent(); dialog.dismiss()
        }
        builder.setNegativeButton("Nein") { dialog, _ ->                                        // "No"
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
        adapter                     = IndexCardsAdapter()
        recyclerView.adapter        = adapter
    }

    private fun initView() {
        edTopic         = findViewById(R.id.edTopic)
        edContent       = findViewById(R.id.edContent)
        btnAdd          = findViewById(R.id.btnAdd)
        btwView         = findViewById(R.id.btnView)
        btnUpdate       = findViewById(R.id.btnUpdate)
        recyclerView    = findViewById(R.id.recyclerView)
    }
}


class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION  = 1
        private const val DATABASE_NAME     = "data.db"
        private const val TBL_Data          = "data_db"
        private const val ID                = "id"
        private const val TOPIC             = "topic"
        private const val CONTENT           = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = (
                "CREATE TABLE " + TBL_Data +
                        "(" + ID + " INTEGER PRIMARY KEY, " + TOPIC + " TEXT," + CONTENT + " TEXT" + ")")
        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(
            """
            DROP TABLE IF EXISTS $TBL_Data
            """.trimIndent())
        onCreate(db)
    }

    fun insertStudent(std: IndexCardsModel): Long{
        val db            = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, std.id); contentValues.put(TOPIC, std.topic); contentValues.put(CONTENT, std.content)

        val success = db.insert(TBL_Data, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getAllContent(): ArrayList<IndexCardsModel> {
        val stdList : ArrayList<IndexCardsModel> = ArrayList()
        val selectQuery =
            """
            SELECT * FROM $TBL_Data
            """.trimIndent()
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        }
        catch (e: Exception) {
            e.printStackTrace(); db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var topic: String
        var content: String

        if(cursor.moveToFirst()){
            do {
                id      = cursor.getInt(cursor.getColumnIndex("id"))
                topic   = cursor.getString(cursor.getColumnIndex("Thema"))
                content = cursor.getString(cursor.getColumnIndex("Inhalt"))
                val std = IndexCardsModel(id = id, topic = topic, content = content)

                stdList.add(std)
            }
            while (cursor.moveToNext())
        }
        return stdList
    }

    fun updateContent(std: IndexCardsModel): Int{
        val db              = this.writableDatabase
        val contentValues   = ContentValues()

        contentValues.put(ID, std.id); contentValues.put(TOPIC, std.topic); contentValues.put(CONTENT, std.content)

        val success = db.update(TBL_Data, contentValues, "id=" + std.id, null)

        db.close()
        return success
    }

    fun deleteContentById(id: Int): Int{
        val db            = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(ID, id)

        val success = db.delete(TBL_Data, "id=$id", null)

        db.close()
        return success
    }
}

class IndexCardsAdapter: RecyclerView.Adapter<IndexCardsAdapter.ContentViewHolder>() {
    private var cntList: ArrayList<IndexCardsModel> = ArrayList()
    private var onClickItem:((IndexCardsModel)->Unit)?= null
    private var onClickDeleteItem:((IndexCardsModel)->Unit)?= null

    @SuppressLint("NotifyDataSetChanged")
    fun addItems(items: ArrayList<IndexCardsModel>){
        this.cntList = items
        notifyDataSetChanged()
    }

    fun setOnClickitem(callback: (IndexCardsModel)->Unit){
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (IndexCardsModel) -> Unit){
        this.onClickDeleteItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContentViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.activity_indexcards_1, parent, false)
    )

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val cnt = cntList[position]
        holder.bindView(cnt)
        holder.itemView.setOnClickListener{ onClickItem?.invoke(cnt)
            holder.btnDelete.setOnClickListener{ onClickDeleteItem?.invoke(cnt) }
        }
    }

    override fun getItemCount(): Int {
        return cntList.size
    }

    class ContentViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        private var id        = view.findViewById<TextView>(R.id.tvId)
        private var topic     = view.findViewById<TextView>(R.id.tvTopic)
        private var content   = view.findViewById<TextView>(R.id.tvContent)
        var btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bindView(std: IndexCardsModel) {
            id.text      = std.id.toString()
            topic.text   = std.topic
            content.text = std.content
        }
    }
}

data class IndexCardsModel(var id: Int = getAutoId(), var topic: String = "", var content: String = "") {
    companion object {
        fun getAutoId():Int{
            val random = Random()
            return random.nextInt(100)
        }
    }
}