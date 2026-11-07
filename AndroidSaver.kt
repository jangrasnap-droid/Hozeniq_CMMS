package com.dharmvir.cmms

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File

/**
 * JavaScript bridge: window.AndroidSaver
 *  - save(name, mime, base64): writes a file to Downloads (exports, QR sheets, slips)
 *  - print(): prints the current page through the Android print service
 *  - printHtml(html): prints generated HTML (QR label sheets)
 */
class AndroidSaver(private val context: Context, private val webView: WebView) {

    private val main = Handler(Looper.getMainLooper())
    private var labelView: WebView? = null

    @JavascriptInterface
    fun save(name: String, mime: String, base64: String) {
        val safeName = name.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val cleanMime = mime.substringBefore(';').ifBlank { "application/octet-stream" }
        val ok = try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            writeToDownloads(safeName, cleanMime, bytes)
        } catch (e: Exception) {
            false
        }
        main.post {
            Toast.makeText(
                context,
                if (ok) "Saved to Downloads: $safeName" else "Could not save $safeName",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun writeToDownloads(name: String, mime: String, bytes: ByteArray): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, name)
                put(MediaStore.Downloads.MIME_TYPE, mime)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false
            resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return false
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            return true
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            File(dir, name).writeBytes(bytes)
            return true
        }
    }

    @JavascriptInterface
    fun print() {
        main.post {
            val pm = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            pm.print("CMMS", webView.createPrintDocumentAdapter("CMMS"), attributes())
        }
    }

    @JavascriptInterface
    fun printHtml(html: String) {
        main.post {
            val wv = WebView(context)
            labelView = wv
            wv.settings.javaScriptEnabled = false
            wv.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    val pm = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                    pm.print("CMMS labels", view.createPrintDocumentAdapter("CMMS labels"), attributes())
                }
            }
            wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        }
    }

    private fun attributes(): PrintAttributes =
        PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()
}
