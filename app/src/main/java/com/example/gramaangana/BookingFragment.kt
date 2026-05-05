package com.example.gramaangana

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class BookingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)
        val nameInput = view.findViewById<EditText>(R.id.input_name)
        val purposeInput = view.findViewById<EditText>(R.id.input_purpose)
        val dateInput = view.findViewById<EditText>(R.id.input_date)
        val startTimeInput = view.findViewById<EditText>(R.id.input_start_time)
        val endTimeInput = view.findViewById<EditText>(R.id.input_end_time)
        val submitBtn = view.findViewById<Button>(R.id.btn_submit)
        val aiBtn = view.findViewById<Button>(R.id.btn_ai_suggest)
        val aiResult = view.findViewById<TextView>(R.id.ai_suggestion)
        val ref = FirebaseDatabase.getInstance().getReference("bookings")

        // Pre-fill name from logged-in user
        val prefs = requireContext().getSharedPreferences("grama_prefs", android.content.Context.MODE_PRIVATE)
        val savedName = prefs.getString("user_name", "") ?: ""
        nameInput.setText(savedName)

        aiBtn.setOnClickListener {
            val purpose = purposeInput.text.toString().trim()
            if (purpose.isEmpty()) {
                Toast.makeText(requireContext(), "Enter purpose first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            aiResult.text = "Getting AI suggestion..."
            lifecycleScope.launch {
                val result = GeminiHelper.getSuggestion(
                    "For a community hall booking with purpose: '$purpose', suggest the best time slot and tips in 2 sentences."
                )
                aiResult.text = "💡 AI Suggestion: $result"
            }
        }

        submitBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val purpose = purposeInput.text.toString().trim()
            val date = dateInput.text.toString().trim()
            val startTime = startTimeInput.text.toString().trim()
            val endTime = endTimeInput.text.toString().trim()
            val userPhone = prefs.getString("user_phone", "") ?: ""

            if (name.isEmpty() || purpose.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ref.orderByChild("date").equalTo(date)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(requireContext(), "❌ Already booked on this date!", Toast.LENGTH_LONG).show()
                        } else {
                            val id = ref.push().key ?: return
                            val booking = mapOf(
                                "name" to name,
                                "purpose" to purpose,
                                "date" to date,
                                "startTime" to startTime,
                                "endTime" to endTime,
                                "status" to "pending",
                                "userPhone" to userPhone
                            )
                            ref.child(id).setValue(booking)
                            Toast.makeText(requireContext(), "✅ Booking request sent!", Toast.LENGTH_LONG).show()
                            purposeInput.text.clear()
                            dateInput.text.clear()
                            startTimeInput.text.clear()
                            endTimeInput.text.clear()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        return view
    }
}