package com.namma_shaale.inventory.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

object FileUtil {
    fun createReportFile(context: Context, reportContent: String): Uri? {
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()

            val fileName = "report_${System.currentTimeMillis()}.txt"
            val file = File(reportsDir, fileName)
            FileWriter(file).use { it.write(reportContent) }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
}
