package com.example.post_grade

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Student_Dashboard : AppCompatActivity() {

    private lateinit var composeChatContainer: LinearLayout
    private lateinit var aiChat: LinearLayout
    private lateinit var name: ImageView

    // Permission launcher for notifications
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_dashboard)

        // --- Initialize Views ---
        aiChat = findViewById(R.id.AIChat) // AI Chat Bar
        composeChatContainer = findViewById(R.id.composeChatContainer)
        name = findViewById(R.id.name)

        // --- System Bars Padding ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AICHAT)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- AI Chat Click ---
        aiChat.setOnClickListener {
            composeChatContainer.visibility = View.VISIBLE

            val composeView = ComposeView(this).apply {
                setContent {
                    FirebaseAiLogicChatScreen(
                        onClose = {
                            composeChatContainer.removeAllViews()
                            composeChatContainer.visibility = View.GONE
                        }
                    )
                }
            }

            composeChatContainer.removeAllViews()
            composeChatContainer.addView(composeView)
        }

        // --- Notifications ---
        ensureNotificationPermission()
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
        name.setOnClickListener {
            notificationHelper.showNotification("Hey!", "This is an immediate notification")
        }

        scheduleDailyNotification(
            context = this,
            hour = 18,
            minute = 30,
            requestCode = 1001,
            title = "Marks are out",
            message = "Study!"
        )
    }

    // --- Dashboard Buttons ---
    fun onQuotationClick(view: View) = startActivity(Intent(this, request_Quotation::class.java))
    fun onSubmitPaperClick(view: View) = startActivity(Intent(this, submit_paper::class.java))
    fun onViewStatusClick(view: View) = startActivity(Intent(this, view_submited::class.java))
    fun onFeedbackClick(view: View) = startActivity(Intent(this, consultant_feedback::class.java))
    fun name(imageView: ImageView) = startActivity(Intent(this, student_register::class.java))
    fun onLogoutClick(view: View) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}