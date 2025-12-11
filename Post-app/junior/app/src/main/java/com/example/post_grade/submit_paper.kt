package com.example.post_grade

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStream

class submit_paper : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var selectedPdfUri: Uri? = null

    private var selectedReason = ""
    private var selectedUrgency = ""
    private val CHUNK_SIZE = 900_000 // ~900 KB per chunk (Base64 increases size slightly)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_paper)

        db = FirebaseFirestore.getInstance()

        val etUniversity = findViewById<EditText>(R.id.etUniversity)
        val etDepartment = findViewById<EditText>(R.id.etDepartment)
        val etQualification = findViewById<EditText>(R.id.etQualification)
        val etResearchArea = findViewById<EditText>(R.id.etResearchArea)
        val etDescription = findViewById<EditText>(R.id.etDescription)

        val btnChooseFile = findViewById<Button>(R.id.btnChooseFile)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        btnSubmit.isEnabled = false

        // PDF Picker
        val pdfPickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    selectedPdfUri = uri
                    Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show()
                    btnSubmit.isEnabled = true
                }
            }

        btnChooseFile.setOnClickListener {
            pdfPickerLauncher.launch("application/pdf")
        }

        // Reason buttons
        val reasonButtons = listOf(
            findViewById<Button>(R.id.btnReasonLanguage),
            findViewById<Button>(R.id.btnReasonData),
            findViewById<Button>(R.id.btnReasonValidity)
        )
        reasonButtons.forEach { button ->
            button.setOnClickListener {
                selectedReason = button.text.toString()
                reasonButtons.forEach { it.alpha = 0.5f }
                button.alpha = 1.0f
            }
        }

        // Urgency buttons
        val urgencyButtons = listOf(
            findViewById<Button>(R.id.btnUrgencyLow),
            findViewById<Button>(R.id.btnUrgencyMedium),
            findViewById<Button>(R.id.btnUrgencyHigh)
        )
        urgencyButtons.forEach { button ->
            button.setOnClickListener {
                selectedUrgency = button.text.toString()
                urgencyButtons.forEach { it.alpha = 0.5f }
                button.alpha = 1.0f
            }
        }

        // Submit button
        btnSubmit.setOnClickListener {
            if (selectedPdfUri == null) {
                Toast.makeText(this, "Select a PDF first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (selectedReason.isEmpty() || selectedUrgency.isEmpty()) {
                Toast.makeText(this, "Select reason and urgency", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedPdfUri!!)
                if (inputStream == null) {
                    Toast.makeText(this, "Unable to open PDF", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val bytes = inputStream.readBytes()

                // Split into chunks
                val chunks = mutableListOf<String>()
                var start = 0
                while (start < bytes.size) {
                    val end = (start + CHUNK_SIZE).coerceAtMost(bytes.size)
                    val chunkBytes = bytes.copyOfRange(start, end)
                    val chunkBase64 = Base64.encodeToString(chunkBytes, Base64.DEFAULT)
                    chunks.add(chunkBase64)
                    start = end
                }

                // Create Firestore document
                val submissionsRef = db.collection("submissions")
                val docId = submissionsRef.document().id
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                val submission = hashMapOf(
                    "Id" to docId,
                    "University" to etUniversity.text.toString(),
                    "Department" to etDepartment.text.toString(),
                    "Qualification" to etQualification.text.toString(),
                    "ResearchArea" to etResearchArea.text.toString(),
                    "Reason" to selectedReason,
                    "Urgency" to selectedUrgency,
                    "Description" to etDescription.text.toString(),
                    "FileChunks" to chunks,
                    "UserId" to currentUserId,
                    "Status" to "Pending",
                    "Progression" to "Awaiting review",
                    "CreatedAt" to Timestamp.now()
                )

                submissionsRef.document(docId)
                    .set(submission)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Submission successful", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(this, "File error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}