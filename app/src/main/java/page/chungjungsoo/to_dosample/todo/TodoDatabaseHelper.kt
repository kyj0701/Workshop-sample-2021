package page.chungjungsoo.to_dosample.todo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TodoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private val DB_NAME = "TodoDB"
        private val DB_VERSION = 1
        private val TABLE_NAME = "todo"
        private val ID = "id"
        private val TITLE = "Title"
        private val DESC = "Desciption"
        private val DATE = "Date"
        private val TIME = "time"
        private val FIN = "Finished"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Database
        val createTable =
            "CREATE TABLE $TABLE_NAME" +
                    "($ID INTEGER PRIMARY KEY," +
                    "$TITLE TEXT," +
                    "$DESC TEXT," +
                    "$DATE TEXT," +
                    "$TIME TEXT," +
                    "$FIN INTEGER DEFAULT 0)"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun addTodo(todo: Todo) : Boolean {
        // Add to-do to database
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(TITLE, todo.title)
        values.put(DESC, todo.description)
        values.put(DATE, todo.date)
        values.put(TIME, todo.time)
        values.put(FIN, booleanToInteger(todo.finished))

        val _success = db.insert(TABLE_NAME, null, values)
        db.close()

        return (Integer.parseInt("$_success") != -1)
    }

    fun delTodo(position: Int) : Boolean {
        // Delete to-do in database by position. (ID values != position) <- IMPORTANT!!
        val db = this.writableDatabase
        val query = db.rawQuery("DELETE FROM $TABLE_NAME WHERE $ID IN(SELECT $ID FROM $TABLE_NAME LIMIT 1 OFFSET $position)", null)
        val num = query.count
        query.close()
        db.close()
        return num == 0
    }

    fun updateTodo(todo: Todo, position: Int) : Boolean {
        // Update to-do in database
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(TITLE, todo.title)
        values.put(DESC, todo.description)
        values.put(DATE, todo.date)
        values.put(TIME, todo.time)
        values.put(FIN, booleanToInteger(todo.finished))

        val result = db.update(TABLE_NAME, values, "$ID IN(SELECT $ID FROM $TABLE_NAME LIMIT 1 OFFSET $position)", null) > 0
        db.close()

        return result
    }

    fun getAll() : MutableList<Todo> {
        // Get all data from database and return MutableList for ListView.
        var allTodo = mutableListOf<Todo>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        var title : String
        var desciption : String
        var date : String
        var time : String
        var finished : Boolean

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(cursor.getColumnIndex(TITLE))
                    desciption = cursor.getString(cursor.getColumnIndex(DESC))
                    date = cursor.getString(cursor.getColumnIndex(DATE))
                    time = cursor.getString(cursor.getColumnIndex(TIME))
                    finished = integerToBoolean(cursor.getInt(cursor.getColumnIndex(FIN)))

                    allTodo.add(Todo(title, desciption, date, time, finished))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()

        return allTodo
    }

    private fun booleanToInteger(bool:Boolean) : Int {
        // SQLite does not have boolean type. Changes boolean to integer
        return if (bool) {
            // True
            1
        } else {
            // False
            0
        }
    }

    private fun integerToBoolean(int: Int) : Boolean {
        // Vice Versa
        return int == 1
    }
}