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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class student_register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_register)



        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get references
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)

        val user = auth.currentUser

        // 🔹 If user is logged in, load their existing profile
        if (user != null) {
            db.collection("students").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etName.setText(doc.getString("name"))
                        etSurname.setText(doc.getString("surname"))
                        etEmail.setText(doc.getString("email"))
                        etUsername.setText(doc.getString("username"))
                        etPassword.setText(doc.getString("password"))
                    }
                }
        }

        // 🔹 Handle Save / Register
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val studentData = hashMapOf(
                "name" to name,
                "surname" to surname,
                "email" to email,
                "username" to username,
                "password" to password, // For production, hash this
                "timestamp" to Timestamp.now()
            )

            if (user != null) {
                // Update existing user info
                db.collection("students").document(user.uid)
                    .set(studentData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // New registration
                db.collection("students")
                    .add(studentData)
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
}