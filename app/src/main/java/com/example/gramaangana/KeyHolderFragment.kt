package com.example.gramaangana

import android.app.AlertDialog
import android.content.Context
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
        val loggedInText = view.findViewById<TextView>(R.id.logged_in_as)
        val logoutBtn = view.findViewById<Button>(R.id.btn_logout)

        // Show logged-in user info
        val prefs = requireContext().getSharedPreferences("grama_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User") ?: "User"
        val userPhone = prefs.getString("user_phone", "") ?: ""
        loggedInText.text = "👤 Logged in as: $userName ($userPhone)"

        // Logout with confirmation
        logoutBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes, Logout") { _, _ ->
                    prefs.edit().clear().apply()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                    Toast.makeText(requireContext(), "✅ Logged out successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        statusText.text = "Loading key holder info..."

        val ref = FirebaseDatabase.getInstance().getReference("keyholder")

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