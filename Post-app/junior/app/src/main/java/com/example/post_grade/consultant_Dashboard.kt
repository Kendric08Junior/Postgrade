package com.example.post_grade

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class consultant_Dashboard : AppCompatActivity() {

    private lateinit var composeChatContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultant_dashboard)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llDashboardContainer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons
        val btnSubmittedPapers = findViewById<LinearLayout>(R.id.btnSubmittedPapers)
        val btnAddProfile = findViewById<LinearLayout>(R.id.btnAddProfile)

        val btnLogout = findViewById<LinearLayout>(R.id.btnLogout)
        val aiChat = findViewById<LinearLayout>(R.id.AIChat)
        composeChatContainer = findViewById(R.id.composeChatContainer)

        // --- AI Chat Click Listener ---
        aiChat.setOnClickListener {
            composeChatContainer.visibility = View.VISIBLE

            val composeView = ComposeView(this).apply {
                setContent {
                    FirebaseAiLogicChatScreen(
                        onClose = {
                            // Close chat overlay
                            composeChatContainer.removeAllViews()
                            composeChatContainer.visibility = View.GONE
                        }
                    )
                }
            }

            composeChatContainer.removeAllViews()
            composeChatContainer.addView(composeView)
        }

        // --- Other Buttons ---
        btnSubmittedPapers.setOnClickListener {
            val intent = Intent(this, consultant_submissions::class.java)
            startActivity(intent)
        }

        btnAddProfile.setOnClickListener {
            val intent = Intent(this, consultant_profile::class.java)
            startActivity(intent)
        }



        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}