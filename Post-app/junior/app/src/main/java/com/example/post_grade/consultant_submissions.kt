package com.example.post_grade

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class consultant_submissions : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var llContainer: LinearLayout
    private lateinit var tvNoSubmissions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consultant_submissions)

        llContainer = findViewById(R.id.llSubmissionContainer)
        tvNoSubmissions = findViewById(R.id.tvNoSubmissions)

        db = FirebaseFirestore.getInstance()
        loadSubmissions()
    }

    private fun loadSubmissions() {
        db.collection("submissions")
            .get()
            .addOnSuccessListener { snapshot ->
                llContainer.removeAllViews()

                if (snapshot.isEmpty) {
                    tvNoSubmissions.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                tvNoSubmissions.visibility = View.GONE

                for (doc in snapshot.documents) {
                    val userId = doc.getString("UserId") ?: ""
                    val department = doc.getString("Department") ?: ""
                    val description = doc.getString("Description") ?: ""
                    val qualification = doc.getString("Qualification") ?: ""
                    val reason = doc.getString("Reason") ?: ""
                    val researchArea = doc.getString("ResearchArea") ?: ""
                    val university = doc.getString("University") ?: ""
                    val urgency = doc.getString("Urgency") ?: ""
                    val fileUrl = doc.getString("FileUrl") ?: ""

                    // Create card
                    val submissionCard = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(24, 24, 24, 24)
                        setBackgroundColor(0xFF1A1123.toInt())
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 0, 0, 24)
                        layoutParams = params
                    }

                    // Add UserId at the top
                    val tvUserId = TextView(this).apply {
                        text = "UserId: $userId"
                        setTextColor(0xFFFFFFFF.toInt())
                        textSize = 16f
                        setTypeface(null, Typeface.BOLD)
                        setPadding(0, 0, 0, 8)
                    }
                    submissionCard.addView(tvUserId)

                    fun addField(label: String, value: String) {
                        val tv = TextView(this).apply {
                            text = "$label: $value"
                            setTextColor(0xFFFFFFFF.toInt())
                            textSize = 14f
                            setTypeface(null, Typeface.BOLD)
                            setPadding(0, 4, 0, 4)
                        }
                        submissionCard.addView(tv)
                    }

                    addField("Department", department)
                    addField("Description", description)
                    addField("Qualification", qualification)
                    addField("Reason", reason)
                    addField("Research Area", researchArea)
                    addField("University", university)
                    addField("Urgency", urgency)
                    addField("FileUrl", fileUrl) // show the file URL

                    llContainer.addView(submissionCard)
                }
            }
            .addOnFailureListener {
                tvNoSubmissions.visibility = View.VISIBLE
            }
    }
}
