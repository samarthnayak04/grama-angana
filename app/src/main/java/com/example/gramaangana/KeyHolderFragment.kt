package com.example.gramaangana

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class KeyHolderFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_keyholder, container, false)

        val nameText = view.findViewById<TextView>(R.id.kh_name)
        val phoneText = view.findViewById<TextView>(R.id.kh_phone)
        val roleText = view.findViewById<TextView>(R.id.kh_role)
        val addressText = view.findViewById<TextView>(R.id.kh_address)
        val timingsText = view.findViewById<TextView>(R.id.kh_timings)
        val callBtn = view.findViewById<Button>(R.id.btn_call)
        val statusText = view.findViewById<TextView>(R.id.kh_status)

        statusText.text = "Loading key holder info..."

        val ref = FirebaseDatabase.getInstance().getReference("keyholder")

        // Seed default key holder if not exists (for demo)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val defaultKH = mapOf(
                        "name" to "Sri Ramesh Gowda",
                        "phone" to "9845012345",
                        "role" to "Panchayat Secretary",
                        "address" to "Door No. 42, Main Road, Near Anjaneya Temple",
                        "timings" to "Mon-Sat: 8:00 AM - 8:00 PM"
                    )
                    ref.setValue(defaultKH)
                }

                val name = snapshot.child("name").getValue(String::class.java) ?: "Sri Ramesh Gowda"
                val phone = snapshot.child("phone").getValue(String::class.java) ?: "9845012345"
                val role = snapshot.child("role").getValue(String::class.java) ?: "Panchayat Secretary"
                val address = snapshot.child("address").getValue(String::class.java) ?: "Main Road"
                val timings = snapshot.child("timings").getValue(String::class.java) ?: "8 AM - 8 PM"

                nameText.text = name
                phoneText.text = "📞 $phone"
                roleText.text = role
                addressText.text = "📍 $address"
                timingsText.text = "🕐 $timings"
                statusText.text = "✅ Contact for hall access"

                callBtn.setOnClickListener {
                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    startActivity(callIntent)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                statusText.text = "❌ Could not load. Check internet."
            }
        })

        return view
    }
}