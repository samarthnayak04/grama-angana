package com.example.gramaangana

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class EventBoardFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_events, container, false)
        val container2 = view.findViewById<LinearLayout>(R.id.events_container)
        val statusText = view.findViewById<TextView>(R.id.events_status)

        statusText.text = "Loading today's events..."

        val ref = FirebaseDatabase.getInstance().getReference("events")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Seed default events if Firebase is empty (for first-time demo)
                if (!snapshot.exists()) {
                    val defaultEvents = mapOf(
                        "evt1" to mapOf("title" to "🏏 Sports Practice", "desc" to "Cricket & Football", "time" to "4:00 PM - 7:00 PM"),
                        "evt2" to mapOf("title" to "🏥 Health Camp", "desc" to "Free checkup by PHC doctors", "time" to "10:00 AM - 2:00 PM"),
                        "evt3" to mapOf("title" to "📋 Panchayat Meeting", "desc" to "Monthly budget discussion", "time" to "6:00 PM - 8:00 PM"),
                        "evt4" to mapOf("title" to "📚 Skill Training", "desc" to "Tailoring workshop for women", "time" to "9:00 AM - 12:00 PM")
                    )
                    ref.setValue(defaultEvents)
                    // Render defaults immediately
                    container2.removeAllViews()
                    for ((_, evt) in defaultEvents) {
                        addEventCard(container2, evt["title"]!!, evt["desc"]!!, evt["time"]!!)
                    }
                    statusText.text = "📅 Showing ${defaultEvents.size} events for today"
                    return
                }

                container2.removeAllViews()
                var count = 0
                for (child in snapshot.children) {
                    val title = child.child("title").getValue(String::class.java) ?: continue
                    val desc = child.child("desc").getValue(String::class.java) ?: ""
                    val time = child.child("time").getValue(String::class.java) ?: ""
                    addEventCard(container2, title, desc, time)
                    count++
                }
                statusText.text = if (count > 0) "📅 Showing $count events for today" else "No events today"
            }

            override fun onCancelled(error: DatabaseError) {
                statusText.text = "❌ Could not load events. Check internet."
            }
        }
        ref.addValueEventListener(listener)

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}
            override fun onViewDetachedFromWindow(v: View) { ref.removeEventListener(listener) }
        })

        return view
    }

    private fun addEventCard(container: LinearLayout, title: String, desc: String, time: String) {
        val card = layoutInflater.inflate(R.layout.item_event, container, false)
        card.findViewById<TextView>(R.id.event_title).text = title
        card.findViewById<TextView>(R.id.event_desc).text = desc
        card.findViewById<TextView>(R.id.event_time).text = time
        container.addView(card)
    }
}