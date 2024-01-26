package com.example.finaldemo
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize CalendarView widget
        calendarView = findViewById(R.id.calendarView)

        // Set date format for CalendarView widget
   //     calendarView.dateFormat == "dd-MM-yyyy"

        // Set OnDateChangeListener for CalendarView widget
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Create Calendar instance and set it to the selected date
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            // Create a DatePickerDialog instance and set its OnDateSetListener to save the selected date
            val datePickerDialog = DatePickerDialog(
                this@CalendarActivity,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    // Save the selected date using SharedPreferences or a database
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
    }
}
