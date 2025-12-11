package com.example.post_grade

import org.junit.Assert
import org.junit.Test

class UploadValidatorTest {

    @Test
    fun testFileExtension_isPdf() {
        val fileName = "submission.pdf"
        Assert.assertTrue(fileName.endsWith(".pdf"))
    }

    @Test
    fun testFileExtension_isNotPdf() {
        val fileName = "submission.docx"
        Assert.assertFalse(fileName.endsWith(".pdf"))
    }
}