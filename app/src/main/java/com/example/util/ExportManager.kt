package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.viewmodel.ThemeState
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExportManager {
    fun exportTheme(context: Context, themeState: ThemeState): Uri? {
        return try {
            val cacheDir = context.cacheDir
            val mtzFile = File(cacheDir, "HyperOS_Theme_${System.currentTimeMillis()}.mtz")
            
            FileOutputStream(mtzFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    // Add description.xml
                    val descriptionXml = """
                        <?xml version="1.0" encoding="utf-8"?>
                        <HyperOS_Theme>
                            <title>Custom Theme</title>
                            <designer>HyperTheme Maker</designer>
                            <author>HyperTheme Maker</author>
                            <version>1.0</version>
                            <uiVersion>14</uiVersion>
                        </HyperOS_Theme>
                    """.trimIndent()
                    
                    zos.putNextEntry(ZipEntry("description.xml"))
                    zos.write(descriptionXml.toByteArray())
                    zos.closeEntry()
                    
                    // Add preview folder (mock data)
                    zos.putNextEntry(ZipEntry("preview/preview_lockscreen_0.jpg"))
                    zos.write(ByteArray(0))
                    zos.closeEntry()
                    
                    // Add icons (mock data)
                    zos.putNextEntry(ZipEntry("icons"))
                    zos.write(ByteArray(0))
                    zos.closeEntry()
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                mtzFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareTheme(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/x-zip-compressed"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "แชร์ธีม (Share Theme)"))
    }
}
