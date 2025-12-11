package com.example.post_grade

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class consulant_register : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consulant_register)




        // Initialize Firestore
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        // Link UI
        val etName = findViewById<EditText>(R.id.etName)
        val etSurname = findViewById<EditText>(R.id.etSurname)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate input
            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create consultant map
            val consultant = hashMapOf(
                "name" to name,
                "surname" to surname,
                "email" to email,
                "username" to username,
                "password" to password, // later can hash
                "timestamp" to Timestamp.now()
            )

            // Save to Firestore under "consultants"
            db.collection("consultants")
                .add(consultant)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // Clear fields
                    etName.text.clear()
                    etSurname.text.clear()
                    etEmail.text.clear()
                    etUsername.text.clear()
                    etPassword.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}