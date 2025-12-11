package com.example.post_grade

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class request_Quotation : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var selectedReason: String? = null
    private lateinit var reasonButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_quotation)

        db = FirebaseFirestore.getInstance()

        // Input fields
        val etUniversity = findViewById<EditText>(R.id.etUniversity)
        val etDepartment = findViewById<EditText>(R.id.etDepartment)
        val etQualification = findViewById<EditText>(R.id.etQualification)
        val etResearchArea = findViewById<EditText>(R.id.etResearchArea)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitRequest)

        // Reason buttons
        val btnReasonMaterials = findViewById<Button>(R.id.btnReasonMaterials)
        val btnReasonTopic = findViewById<Button>(R.id.btnReasonTopic)
        val btnReasonStrategies = findViewById<Button>(R.id.btnReasonStrategies)
        val btnReasonSoftware = findViewById<Button>(R.id.btnReasonSoftware)

        reasonButtons = listOf(btnReasonMaterials, btnReasonTopic, btnReasonStrategies, btnReasonSoftware)

        // Set up reason selection behavior
        reasonButtons.forEach { button ->
            button.setOnClickListener {
                selectedReason = button.text.toString()
                highlightSelectedReason(button)
            }
        }

        // Submit button action
        btnSubmit.setOnClickListener {
            val university = etUniversity.text.toString().trim()
            val department = etDepartment.text.toString().trim()
            val qualification = etQualification.text.toString().trim()
            val researchArea = etResearchArea.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (university.isEmpty() || department.isEmpty() ||
                qualification.isEmpty() || researchArea.isEmpty() || selectedReason == null
            ) {
                Toast.makeText(this, "Please complete all required fields and select a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quotationData = hashMapOf(
                "university" to university,
                "department" to department,
                "qualification" to qualification,
                "researchArea" to researchArea,
                "description" to description,
                "reason" to selectedReason,
                "timestamp" to Timestamp.now()
            )

            db.collection("quotations")
                .add(quotationData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quotation request submitted!", Toast.LENGTH_LONG).show()
                    etUniversity.text.clear()
                    etDepartment.text.clear()
                    etQualification.text.clear()
                    etResearchArea.text.clear()
                    etDescription.text.clear()
                    resetReasonButtons()
                    selectedReason = null
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun highlightSelectedReason(selectedButton: Button) {
        // Reset all buttons to default
        reasonButtons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            it.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

        // Highlight the selected one
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_purple))
        selectedButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    private fun resetReasonButtons() {
        reasonButtons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            it.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }
}