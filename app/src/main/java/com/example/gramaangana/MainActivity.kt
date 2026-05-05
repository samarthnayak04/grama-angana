package com.example.gramaangana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EventBoardFragment()).commit()
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_events -> EventBoardFragment()
                R.id.nav_calendar -> CalendarFragment()
                R.id.nav_booking -> BookingFragment()
                R.id.nav_mybookings -> MyBookingsFragment()
                R.id.nav_keyholder -> KeyHolderFragment()
                else -> EventBoardFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()
            true
        }
    }
}