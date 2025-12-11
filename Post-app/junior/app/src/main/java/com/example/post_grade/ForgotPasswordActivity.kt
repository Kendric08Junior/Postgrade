package com.example.post_grade

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSendResetLink: Button
    private lateinit var tvBackToLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.forgot_password)


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        btnSendResetLink = findViewById(R.id.btnSendResetLink)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        // Send reset link
        btnSendResetLink.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset link: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Back to login
        tvBackToLogin.setOnClickListener {
            finish() // closes this activity and returns to login
        }
    }
}