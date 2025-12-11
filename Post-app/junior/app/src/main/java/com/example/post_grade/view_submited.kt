package com.example.post_grade

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class view_submited : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_submited)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        container = findViewById(R.id.container)

        loadSubmissions()
    }

    private fun loadSubmissions() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("submissions")
            .whereEqualTo("UserId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                container.removeAllViews()

                if (snapshot.isEmpty) {
                    val tv = TextView(this).apply {
                        text = "No submissions yet."
                        setTextColor(resources.getColor(android.R.color.white))
                        textSize = 14f
                        gravity = Gravity.CENTER
                    }
                    container.addView(tv)
                    return@addOnSuccessListener
                }

                for (doc in snapshot.documents) {
                    val evaluations = doc.get("Evaluations") as? List<Map<String, Any>>

                    val statusText = if (evaluations.isNullOrEmpty()) "In Progress" else "Feedback Available"

                    // For each evaluation, show file name
                    if (!evaluations.isNullOrEmpty()) {
                        for (eval in evaluations) {
                            val fileUrl = eval["FileUrl"] as? String ?: "Unknown file"

                            val itemLayout = LinearLayout(this).apply {
                                orientation = LinearLayout.HORIZONTAL
                                setPadding(16, 16, 16, 16)
                                setBackgroundColor(0xFF1A1123.toInt())
                                val params = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                params.setMargins(0, 8, 0, 8)
                                layoutParams = params
                            }

                            val tvPaperName = TextView(this).apply {
                                text = fileUrl.substringAfterLast("/") // Only the filename
                                setTextColor(resources.getColor(android.R.color.white))
                                textSize = 16f
                                setTypeface(null, android.graphics.Typeface.BOLD)
                            }

                            val tvStatus = TextView(this).apply {
                                text = statusText
                                setTextColor(if (statusText == "In Progress") 0xFFFFA500.toInt() else 0xFF00FF00.toInt())
                                textSize = 14f
                                setPadding(16, 0, 0, 0)
                            }

                            itemLayout.addView(tvPaperName, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                            itemLayout.addView(tvStatus)

                            container.addView(itemLayout)
                        }
                    } else {
                        // If no evaluation, still show submission as "In Progress"
                        val fileUrl = doc.getString("FileUrl") ?: "Unknown file"
                        val itemLayout = LinearLayout(this).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(16, 16, 16, 16)
                            setBackgroundColor(0xFF1A1123.toInt())
                            val params = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            params.setMargins(0, 8, 0, 8)
                            layoutParams = params
                        }

                        val tvPaperName = TextView(this).apply {
                            text = fileUrl.substringAfterLast("/") // filename
                            setTextColor(resources.getColor(android.R.color.white))
                            textSize = 16f
                            setTypeface(null, android.graphics.Typeface.BOLD)
                        }

                        val tvStatus = TextView(this).apply {
                            text = statusText
                            setTextColor(if (statusText == "In Progress") 0xFFFFA500.toInt() else 0xFF00FF00.toInt())
                            textSize = 14f
                            setPadding(16, 0, 0, 0)
                        }

                        itemLayout.addView(tvPaperName, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                        itemLayout.addView(tvStatus)

                        container.addView(itemLayout)
                    }
                }
            }
            .addOnFailureListener {
                val tv = TextView(this).apply {
                    text = "Failed to load submissions."
                    setTextColor(resources.getColor(android.R.color.white))
                    textSize = 14f
                    gravity = Gravity.CENTER
                }
                container.addView(tv)
            }
    }
}