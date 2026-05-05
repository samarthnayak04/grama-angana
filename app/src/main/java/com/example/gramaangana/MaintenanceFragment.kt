package com.example.gramaangana

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class MaintenanceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_maintenance, container, false)
        val container2 = view.findViewById<LinearLayout>(R.id.maintenance_container)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.get(requireContext())
            // Seed data if empty
            if (db.maintenanceDao().getAll().isEmpty()) {
                db.maintenanceDao().insert(MaintenanceItem(name = "🪟 New Fan", raised = 300, target = 500))
                db.maintenanceDao().insert(MaintenanceItem(name = "🪑 Chairs (x5)", raised = 800, target = 2000))
                db.maintenanceDao().insert(MaintenanceItem(name = "💡 Bulbs (x10)", raised = 160, target = 200))
                db.maintenanceDao().insert(MaintenanceItem(name = "🚪 Door Repair", raised = 1200, target = 3000))
            }
            val items = db.maintenanceDao().getAll()

            withContext(Dispatchers.Main) {
                for (item in items) {
                    val card = layoutInflater.inflate(R.layout.item_maintenance, container2, false)
                    card.findViewById<TextView>(R.id.item_name).text = item.name
                    card.findViewById<TextView>(R.id.item_amount).text = "₹${item.raised} raised of ₹${item.target}"
                    val percent = (item.raised * 100) / item.target
                    card.findViewById<ProgressBar>(R.id.item_progress).progress = percent
                    card.findViewById<TextView>(R.id.item_percent).text = "$percent% funded"
                    card.findViewById<Button>(R.id.btn_pledge).setOnClickListener {
                        Toast.makeText(requireContext(), "✅ Thank you for pledging to ${item.name}!", Toast.LENGTH_SHORT).show()
                    }
                    container2.addView(card)
                }
            }
        }
        return view
    }
}