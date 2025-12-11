package com.example.post_grade

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class consultant_profile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_profile2)

        db = FirebaseFirestore.getInstance()

        // UI References
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etDOB = findViewById<EditText>(R.id.etDOB)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSaveAllChanges = findViewById<Button>(R.id.btnSaveAllChanges)
        val btnSaveEmail = findViewById<Button>(R.id.btnSaveEmail)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)

        // 📅 Date picker
        etDOB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    etDOB.setText(formattedDate)
                },
                year, month, day
            )
            datePicker.show()
        }

        // 💾 Save all changes
        btnSaveAllChanges.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val consultantData = hashMapOf(
                "name" to firstName,
                "surname" to lastName,
                "username" to username,
                "email" to email,
                "password" to "Jwrld#0808", // if you want to use a static or previously saved password
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("consultants")
                .document(username)
                .set(consultantData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Consultant profile saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // ✉️ Save Email only
        btnSaveEmail.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Enter username and email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("consultants")
                .document(username)
                .update("email", email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // 🔑 Change password placeholder
        btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Password change feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}