package com.example.gramaangana

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class MyBookingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_my_bookings, container, false)
        val container2 = view.findViewById<LinearLayout>(R.id.my_bookings_container)
        val statusText = view.findViewById<TextView>(R.id.my_bookings_status)

        val prefs = requireContext().getSharedPreferences("grama_prefs", Context.MODE_PRIVATE)
        val userPhone = prefs.getString("user_phone", "") ?: ""

        if (userPhone.isEmpty()) {
            statusText.text = "Please login to see your bookings"
            return view
        }

        statusText.text = "Loading your bookings..."

        val query = FirebaseDatabase.getInstance().getReference("bookings")
            .orderByChild("userPhone").equalTo(userPhone)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                container2.removeAllViews()
                if (!snapshot.exists()) {
                    statusText.text = "📭 You have no bookings yet"
                    return
                }
                var count = 0
                for (child in snapshot.children) {
                    val purpose = child.child("purpose").getValue(String::class.java) ?: ""
                    val date = child.child("date").getValue(String::class.java) ?: ""
                    val startTime = child.child("startTime").getValue(String::class.java) ?: ""
                    val endTime = child.child("endTime").getValue(String::class.java) ?: ""
                    val status = child.child("status").getValue(String::class.java) ?: "pending"

                    val card = layoutInflater.inflate(R.layout.item_my_booking, container2, false)
                    card.findViewById<TextView>(R.id.mb_purpose).text = purpose
                    card.findViewById<TextView>(R.id.mb_date).text = "📅 $date"
                    card.findViewById<TextView>(R.id.mb_time).text = "🕐 $startTime - $endTime"

                    val statusView = card.findViewById<TextView>(R.id.mb_status)
                    when (status) {
                        "approved" -> {
                            statusView.text = "✅ Approved"
                            statusView.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                        }
                        "rejected" -> {
                            statusView.text = "❌ Rejected"
                            statusView.setTextColor(android.graphics.Color.RED)
                        }
                        else -> {
                            statusView.text = "⏳ Pending"
                            statusView.setTextColor(android.graphics.Color.parseColor("#F57C00"))
                        }
                    }
                    container2.addView(card)
                    count++
                }
                statusText.text = "📋 You have $count booking(s)"
            }

            override fun onCancelled(error: DatabaseError) {
                statusText.text = "❌ Could not load bookings"
            }
        }

        query.addValueEventListener(listener)

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}
            override fun onViewDetachedFromWindow(v: View) {
                query.removeEventListener(listener)
            }
        })

        return view
    }
}