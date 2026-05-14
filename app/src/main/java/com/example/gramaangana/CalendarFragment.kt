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

        val bookedDates = mutableMapOf<String, Triple<String, String, String>>()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var selectedDate: String? = null

        fun showStatus(selected: String) {
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

        val ref = FirebaseDatabase.getInstance().getReference("bookings")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookedDates.clear()
                for (child in snapshot.children) {
                    val status = child.child("status").getValue(String::class.java) ?: "pending"
                    if (status == "rejected") continue
                    val date = child.child("date").getValue(String::class.java) ?: continue
                    val purpose = child.child("purpose").getValue(String::class.java) ?: "Booked"
                    val startTime = child.child("startTime").getValue(String::class.java) ?: ""
                    val endTime = child.child("endTime").getValue(String::class.java) ?: ""
                    bookedDates[date] = Triple(purpose, startTime, endTime)
                }
                // Re-evaluate the currently selected date whenever data updates
                selectedDate?.let { showStatus(it) }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)

        calendar.setOnDateChangeListener { _, year, month, day ->
            val selected = fmt.format(GregorianCalendar(year, month, day).time)
            selectedDate = selected
            showStatus(selected)
        }

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}
            override fun onViewDetachedFromWindow(v: View) {
                ref.removeEventListener(listener)
            }
        })

        return view
    }
}