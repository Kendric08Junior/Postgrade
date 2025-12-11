package com.example.post_grade

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class consultant_feedback : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var feedbackContainer: LinearLayout
    private lateinit var tvNoFeedback: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultant_feedback)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        feedbackContainer = findViewById(R.id.feedbackContainer)
        tvNoFeedback = findViewById(R.id.tvNoFeedback)

        loadFeedback()
    }

    private fun loadFeedback() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("submissions")
            .whereEqualTo("UserId", userId) // Filter for current student
            .get()
            .addOnSuccessListener { snapshot ->
                feedbackContainer.removeAllViews()

                if (snapshot.isEmpty) {
                    tvNoFeedback.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvNoFeedback.visibility = View.GONE

                for (doc in snapshot.documents) {
                    val evaluations = doc.get("Evaluations") as? List<Map<String, Any>> ?: continue

                    for (eval in evaluations) {
                        val feedbackText = eval["Feedback"] as? String ?: "No feedback"
                        val rating = eval["Rating"]?.toString() ?: "No rating"
                        val mark = eval["Mark"] as? String ?: "No mark"
                        val consultantId = eval["SubmittedConsultantId"] as? String ?: "Unknown"
                        val fileUrl = eval["FileUrl"] as? String

                        // Display feedback info
                        val tvFeedback = TextView(this).apply {
                            text = "Feedback: $feedbackText\nRating: $rating\nMark: $mark\nConsultant ID: $consultantId"
                            setTextColor(resources.getColor(android.R.color.white))
                            setPadding(0, 16, 0, 8)
                        }
                        feedbackContainer.addView(tvFeedback)

                        // Display PDF button if available
                        if (!fileUrl.isNullOrEmpty()) {
                            val btnPdf = Button(this).apply {
                                text = "View PDF"
                                setOnClickListener {
                                    try {
                                        // Use a public URL or your hosting server
                                        val publicUrl = fileUrl.removePrefix("/") // adjust if hosted
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(publicUrl))
                                        startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@consultant_feedback,
                                            "Cannot open PDF",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            feedbackContainer.addView(btnPdf)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load feedback", Toast.LENGTH_SHORT).show()
            }
    }
}