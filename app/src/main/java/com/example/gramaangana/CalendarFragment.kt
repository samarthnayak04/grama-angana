package com.example.gramaangana

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val calendar = view.findViewById<CalendarView>(R.id.calendar_view)
        val statusText = view.findViewById<TextView>(R.id.booking_status)

        // Map of date -> booking info (purpose + start + end time)
        val bookedDates = mutableMapOf<String, Triple<String, String, String>>()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        FirebaseDatabase.getInstance().getReference("bookings")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookedDates.clear()
                    for (child in snapshot.children) {
                        val date = child.child("date").getValue(String::class.java) ?: continue
                        val purpose = child.child("purpose").getValue(String::class.java) ?: "Booked"
                        val startTime = child.child("startTime").getValue(String::class.java) ?: ""
                        val endTime = child.child("endTime").getValue(String::class.java) ?: ""
                        bookedDates[date] = Triple(purpose, startTime, endTime)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        calendar.setOnDateChangeListener { _, year, month, day ->
            val selected = fmt.format(GregorianCalendar(year, month, day).time)
            val booking = bookedDates[selected]
            if (booking != null) {
                val (purpose, start, end) = booking
                statusText.text = if (start.isNotEmpty() && end.isNotEmpty()) {
                    "❌ BOOKED on $selected\nFrom $start to $end\nPurpose: $purpose"
                } else {
                    "❌ BOOKED on $selected\nPurpose: $purpose"
                }
                statusText.setTextColor(android.graphics.Color.RED)
            } else {
                statusText.text = "✅ FREE on $selected\nYou can book this date!"
                statusText.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            }
        }
        return view
    }
}