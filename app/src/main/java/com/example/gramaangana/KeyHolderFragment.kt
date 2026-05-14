package com.example.gramaangana

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class KeyHolderFragment : Fragment() {

    private var bookingsListener: ValueEventListener? = null
    private var bookingsRef: DatabaseReference? = null

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
        val adminHeader = view.findViewById<TextView>(R.id.admin_header)
        val adminContainer = view.findViewById<LinearLayout>(R.id.admin_bookings_container)

        val prefs = requireContext().getSharedPreferences("grama_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "User") ?: "User"
        val userPhone = prefs.getString("user_phone", "") ?: ""
        loggedInText.text = "👤 Logged in as: $userName ($userPhone)"

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

                // Show admin panel if the logged-in user is the keyholder
                if (userPhone == phone) {
                    adminHeader.visibility = View.VISIBLE
                    adminContainer.visibility = View.VISIBLE
                    loadPendingBookings(adminContainer)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                statusText.text = "❌ Could not load. Check internet."
            }
        })

        return view
    }

    private fun loadPendingBookings(container: LinearLayout) {
        val ref = FirebaseDatabase.getInstance().getReference("bookings")
        bookingsRef = ref

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return
                container.removeAllViews()
                var count = 0

                for (child in snapshot.children) {
                    val status = child.child("status").getValue(String::class.java) ?: "pending"
                    if (status != "pending") continue

                    val bookingId = child.key ?: continue
                    val bookerName = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val purpose = child.child("purpose").getValue(String::class.java) ?: ""
                    val date = child.child("date").getValue(String::class.java) ?: ""
                    val startTime = child.child("startTime").getValue(String::class.java) ?: ""
                    val endTime = child.child("endTime").getValue(String::class.java) ?: ""

                    val card = buildBookingCard(bookingId, bookerName, purpose, date, startTime, endTime)
                    container.addView(card)
                    count++
                }

                if (count == 0) {
                    val empty = TextView(requireContext())
                    empty.text = "✅ No pending requests"
                    empty.setTextColor(Color.parseColor("#2E7D32"))
                    empty.setPadding(16, 16, 16, 16)
                    container.addView(empty)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        ref.addValueEventListener(listener)
        bookingsListener = listener
    }

    private fun buildBookingCard(
        bookingId: String, name: String, purpose: String,
        date: String, startTime: String, endTime: String
    ): View {
        val ctx = requireContext()
        val card = CardView(ctx).apply {
            radius = 12f
            cardElevation = 4f
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 16)
            layoutParams = lp
        }

        val inner = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        inner.addView(TextView(ctx).apply {
            text = "👤 $name"
            textSize = 16f
            setTextColor(Color.parseColor("#212121"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        inner.addView(TextView(ctx).apply {
            text = "📋 $purpose"
            textSize = 14f
            setTextColor(Color.parseColor("#424242"))
            setPadding(0, 4, 0, 0)
        })
        inner.addView(TextView(ctx).apply {
            text = "📅 $date   🕐 $startTime - $endTime"
            textSize = 13f
            setTextColor(Color.parseColor("#666666"))
            setPadding(0, 4, 0, 12)
        })

        val btnRow = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val approveBtn = Button(ctx).apply {
            text = "✅ Approve"
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#2E7D32"))
            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            lp.setMargins(0, 0, 8, 0)
            layoutParams = lp
        }
        val rejectBtn = Button(ctx).apply {
            text = "❌ Reject"
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#C62828"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        approveBtn.setOnClickListener {
            updateBookingStatus(bookingId, "approved")
        }
        rejectBtn.setOnClickListener {
            updateBookingStatus(bookingId, "rejected")
        }

        btnRow.addView(approveBtn)
        btnRow.addView(rejectBtn)
        inner.addView(btnRow)
        card.addView(inner)
        return card
    }

    private fun updateBookingStatus(bookingId: String, status: String) {
        FirebaseDatabase.getInstance().getReference("bookings")
            .child(bookingId).child("status").setValue(status)
            .addOnSuccessListener {
                if (isAdded) {
                    val msg = if (status == "approved") "✅ Booking approved!" else "❌ Booking rejected"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bookingsListener?.let { bookingsRef?.removeEventListener(it) }
    }
}
