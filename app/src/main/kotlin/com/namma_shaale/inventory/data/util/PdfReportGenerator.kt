package com.namma_shaale.inventory.data.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.namma_shaale.inventory.data.local.entities.Asset
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

class PdfReportGenerator(private val context: Context) {

    fun generateInventoryReport(
        assets: List<Asset>,
        schoolName: String = "Namma Shaale Government School"
    ): Uri? {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }
        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Page info: A4 size is roughly 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var y = 60f
        
        // Header
        canvas.drawText(schoolName, 50f, y, titlePaint)
        y += 35f
        canvas.drawText("Monthly Inventory Status Report", 50f, y, headerPaint)
        y += 20f
        val dateStr = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on: $dateStr", 50f, y, textPaint)
        y += 40f

        // Stats Summary
        val total = assets.size
        val working = assets.count { it.status == "GREEN" }
        val repair = assets.count { it.status == "YELLOW" }
        val broken = assets.count { it.status == "RED" }

        canvas.drawText("Summary:", 50f, y, headerPaint)
        y += 25f
        canvas.drawText("Total Assets: $total", 70f, y, textPaint)
        y += 18f
        canvas.drawText("Working: $working", 70f, y, textPaint)
        y += 18f
        canvas.drawText("Needs Repair: $repair", 70f, y, textPaint)
        y += 18f
        canvas.drawText("Broken/Lost: $broken", 70f, y, textPaint)
        y += 45f

        // Table Header
        canvas.drawText("Asset Name", 50f, y, headerPaint)
        canvas.drawText("Room/Loc", 220f, y, headerPaint)
        canvas.drawText("Category", 350f, y, headerPaint)
        canvas.drawText("Status", 490f, y, headerPaint)
        y += 10f
        canvas.drawLine(50f, y, 545f, y, paint)
        y += 25f

        // Assets List
        assets.forEachIndexed { index, asset ->
            if (y > 780) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                // Redraw table header on new page
                canvas.drawText("Asset Name", 50f, y, headerPaint)
                canvas.drawText("Room/Loc", 220f, y, headerPaint)
                canvas.drawText("Category", 350f, y, headerPaint)
                canvas.drawText("Status", 490f, y, headerPaint)
                y += 10f
                canvas.drawLine(50f, y, 545f, y, paint)
                y += 25f
            }
            
            canvas.drawText(asset.name.take(20), 50f, y, textPaint)
            canvas.drawText(asset.location ?: "-", 220f, y, textPaint)
            canvas.drawText(asset.category.take(15), 350f, y, textPaint)
            
            // Color code the status
            val statusPaint = Paint().apply {
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = when (asset.status) {
                    "GREEN" -> Color.parseColor("#2E7D32") // Dark Green
                    "YELLOW" -> Color.parseColor("#EF6C00") // Dark Orange
                    "RED" -> Color.parseColor("#C62828") // Dark Red
                    else -> Color.BLACK
                }
            }
            canvas.drawText(asset.status, 490f, y, statusPaint)
            
            y += 20f
            // Subtle horizontal line for each row
            val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 0.5f }
            canvas.drawLine(50f, y - 5, 545f, y - 5, linePaint)
        }

        pdfDocument.finishPage(page)

        // Save file
        val fileName = "Inventory_Report_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            
            FileProvider.getUriForFile(
                context,
                "com.namma_shaale.inventory.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun downloadToPublicStorage(uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val fileName = "Inventory_Report_${System.currentTimeMillis()}.pdf"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val outputUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?: return false
                resolver.openOutputStream(outputUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                true
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
