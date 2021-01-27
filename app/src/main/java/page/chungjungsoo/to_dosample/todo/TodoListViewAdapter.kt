package page.chungjungsoo.to_dosample.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import page.chungjungsoo.to_dosample.R
import java.util.*


class TodoListViewAdapter (context: Context, var resource: Int, var items: MutableList<Todo> ) : ArrayAdapter<Todo>(context, resource, items){
    private lateinit var db: TodoDatabaseHelper

    override fun getView(position: Int, convertView: View?, p2: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        val title : TextView = view.findViewById(R.id.listTitle)
        val description : TextView = view.findViewById(R.id.listDesciption)
        val date : TextView = view.findViewById(R.id.todoDate)
        val edit : Button = view.findViewById(R.id.editBtn)
        val delete : Button = view.findViewById(R.id.delBtn)

        db = TodoDatabaseHelper(this.context)

        // Get to-do item
        var todo = items[position]

        // Load title and description to single ListView item
        title.text = todo.title
        description.text = todo.description
        date.text = todo.date

        // OnClick Listener for edit button on every ListView items
        edit.setOnClickListener {
            // Very similar to the code in MainActivity.kt
            val builder = AlertDialog.Builder(this.context)
            val dialogView = layoutInflater.inflate(R.layout.add_todo_dialog, null)
            val titleToAdd = dialogView.findViewById<EditText>(R.id.todoTitle)
            val desciptionToAdd = dialogView.findViewById<EditText>(R.id.todoDescription)
            val duedateToAdd = dialogView.findViewById<Button>(R.id.dueDate)
            val datetextToAdd = dialogView.findViewById<TextView>(R.id.dateText)
            val timetextToAdd = dialogView.findViewById<TextView>(R.id.timeText)
            val finishedToAdd = dialogView.findViewById<CheckBox>(R.id.todoFinished)
            val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            titleToAdd.setText(todo.title)
            desciptionToAdd.setText(todo.description)
            datetextToAdd.setText(todo.date)
            timetextToAdd.setText(todo.time)
            finishedToAdd.isChecked = todo.finished

            duedateToAdd.setOnClickListener {
                ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                val now = GregorianCalendar()
                val hour: Int = now.get(Calendar.HOUR)
                val minute: Int = now.get(Calendar.MINUTE)

                val dlg2 = TimePickerDialog( this.context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> timetextToAdd.text = "${hourOfDay}:${minute}"}, hour, minute, true)

                dlg2.show()

                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)

                val dlg = DatePickerDialog(this.context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> datetextToAdd.text = "${year}년 ${month+1}월 ${dayOfMonth}일" }, year, month, date)

                dlg.show()
            }

            finishedToAdd.setOnCheckedChangeListener {_, isChecked ->
                finishedToAdd.isChecked = isChecked
            }

            titleToAdd.requestFocus()
            ime.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            builder.setView(dialogView)
                .setPositiveButton("수정") { _, _ ->
                    val tmp = Todo(
                        titleToAdd.text.toString(),
                        desciptionToAdd.text.toString(),
                        datetextToAdd.text.toString(),
                        timetextToAdd.text.toString(),
                        finishedToAdd.isChecked
                    )

                    val result = db.updateTodo(tmp, position)
                    if (result) {
                        todo.title = titleToAdd.text.toString()
                        todo.description = desciptionToAdd.text.toString()
                        todo.date = datetextToAdd.text.toString()
                        todo.time = timetextToAdd.text.toString()
                        todo.finished = finishedToAdd.isChecked
                        notifyDataSetChanged()
                        ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                    }
                    else {
                        Toast.makeText(this.context, "수정 실패! :(", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("취소") {_, _ ->
                    // Cancel Btn. Do nothing. Close keyboard.
                    ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                }
                .show()
        }

        // OnClick Listener for X(delete) button on every ListView items
        delete.setOnClickListener {
            val result = db.delTodo(position)
            if (result) {
                items.removeAt(position)
                notifyDataSetChanged()
            }
            else {
                Toast.makeText(this.context, "삭제 실패! :(", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }

        return view
    }
}