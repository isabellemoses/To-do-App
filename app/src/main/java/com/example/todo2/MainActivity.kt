package com.example.todo2

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    lateinit var db: SQLiteDatabase
    lateinit var tk: EditText
    lateinit var list: ListView
    lateinit var del_tk: EditText
    lateinit var altask: EditText
    lateinit var uptask: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = findViewById<ListView>(R.id.list)
        db = openOrCreateDatabase("tasklist1", Context.MODE_PRIVATE, null)
        db.execSQL(("CREATE TABLE IF NOT EXISTS task1(todo varchar);"))
        retrieveTasks()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ad_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                showDialog()
                return true
            }

            R.id.delete -> {
                DeleteTask()
                return true
            }
            R.id.edit ->
            {
                Edittask()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.dialog_box, null)
        tk = dialogLayout.findViewById<EditText>(R.id.task)
        //val txt_view = dialogLayout.findViewById<TextView>(R.id.txt_view)
        with(builder) {
            setTitle("Add a new task")
            setPositiveButton("Add") { dialog, which ->
                db.execSQL("INSERT INTO task1(todo) VALUES ('" + tk.text.toString() + "');")
                Toast.makeText(applicationContext, "Task Saved", Toast.LENGTH_LONG).show()
                //txt_view.text = editText.text.toString()
                retrieveTasks()
            }
            setNegativeButton("Back") { dialog, which ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }

    @SuppressLint("Range")
    fun retrieveTasks() {
        val cursor: Cursor = db.rawQuery("SELECT * FROM task1", null)
        val taskList = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val task = cursor.getString(cursor.getColumnIndex("todo"))
            taskList.add(task)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        list.adapter = adapter
    }

    @SuppressLint("MissingInflatedId")
    private fun DeleteTask() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.delete_dialog_box, null)
        del_tk = dialogLayout.findViewById<EditText>(R.id.del_task)
        with(builder) {
            setTitle("Enter completed task")
            setPositiveButton("Delete") { dialog, which ->
                val c: Cursor = db.rawQuery("SELECT * FROM task1", null);
                if (c.moveToFirst()) {
                    db.execSQL("DELETE FROM task1 WHERE todo = '" + del_tk.text.toString() + "'");
                    Toast.makeText(applicationContext, "Task Completed", Toast.LENGTH_LONG).show()
                    retrieveTasks()
                }
            }
            setNegativeButton("Back") { dialog, which ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun Edittask() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.edit_layout, null)
        altask = dialogLayout.findViewById<EditText>(R.id.already_task)
        uptask = dialogLayout.findViewById<EditText>(R.id.up_task)
        with(builder) {
            setTitle("Enter task to be edited")
            setPositiveButton("Update") { dialog, which ->
                val c: Cursor = db.rawQuery("SELECT * FROM task1", null)
                if (c.moveToFirst()) {
                    db.execSQL(("UPDATE task1 set todo='" + uptask.text.toString() + "'WHERE todo='" + altask.text.toString() + "'"));
                    Toast.makeText(applicationContext, "Task Updated", Toast.LENGTH_LONG).show()
                    retrieveTasks()
                }
            }
            setNegativeButton("Back") { dialog, which ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()

        }
    }
}
