package com.example.gramaangana

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    private var isSignupMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, skip to MainActivity
        val prefs = getSharedPreferences("grama_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val titleText = findViewById<TextView>(R.id.title_text)
        val nameInput = findViewById<EditText>(R.id.input_name)
        val phoneInput = findViewById<EditText>(R.id.input_phone)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val submitBtn = findViewById<Button>(R.id.btn_submit)
        val toggleBtn = findViewById<TextView>(R.id.toggle_mode)

        // Default mode: Login
        nameInput.visibility = android.view.View.GONE
        titleText.text = "Login to Grama-Angana"
        submitBtn.text = "Login"
        toggleBtn.text = "New user? Sign up here"

        toggleBtn.setOnClickListener {
            isSignupMode = !isSignupMode
            if (isSignupMode) {
                nameInput.visibility = android.view.View.VISIBLE
                titleText.text = "Sign Up for Grama-Angana"
                submitBtn.text = "Sign Up"
                toggleBtn.text = "Already have an account? Login"
            } else {
                nameInput.visibility = android.view.View.GONE
                titleText.text = "Login to Grama-Angana"
                submitBtn.text = "Login"
                toggleBtn.text = "New user? Sign up here"
            }
        }

        submitBtn.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter phone and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.length != 10) {
                Toast.makeText(this, "Enter a valid 10-digit phone", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isSignupMode && name.isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ref = FirebaseDatabase.getInstance().getReference("users").child(phone)

            if (isSignupMode) {
                // Signup: check if user already exists
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(this@LoginActivity, "❌ User already exists. Please login.", Toast.LENGTH_LONG).show()
                        } else {
                            val user = mapOf("name" to name, "phone" to phone, "password" to password)
                            ref.setValue(user)
                            saveLoginAndContinue(prefs, name, phone)
                            Toast.makeText(this@LoginActivity, "✅ Sign up successful!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // Login: verify password
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(this@LoginActivity, "❌ User not found. Please sign up.", Toast.LENGTH_LONG).show()
                            return
                        }
                        val savedPassword = snapshot.child("password").getValue(String::class.java)
                        val savedName = snapshot.child("name").getValue(String::class.java) ?: "User"
                        if (savedPassword == password) {
                            saveLoginAndContinue(prefs, savedName, phone)
                            Toast.makeText(this@LoginActivity, "✅ Welcome back, $savedName!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "❌ Wrong password", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun saveLoginAndContinue(prefs: android.content.SharedPreferences, name: String, phone: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_name", name)
            .putString("user_phone", phone)
            .apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}