package kz.ninebuttons.fotamaster

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    companion object {
        const val STORAGE_PERMISSION_CODE = 23
    }

//    private val stringCommand = arrayOf(
//        "tail -n 30 /ota/FOTA/Log/fotaMaster.log",
//        "tail -n 30 /storage/emulated/0/ota/FOTA/Log/fotaMaster.log",
//        "tail -n 30 ota/FOTA/Log/fotaMaster.log",
//        "tail -n 30 fotaMaster.log",
//        "tail -f /ota/FOTA/Log/fotaMaster.log",
//        "tail -f ota/FOTA/Log/fotaMaster.log",
//        "tail -f fotaMaster.log",
//        "logcat -d")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }



        requestForStoragePermissions()

        val build = "manufacturer: ${Build.MANUFACTURER}, model: ${Build.MODEL}, version SDK: ${Build.VERSION.SDK_INT}, Android version: ${Build.VERSION.RELEASE}"
        findViewById<WebView>(R.id.webView).loadData(build, "text/html", "UTF-8")

//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stringCommand)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        val spinner = findViewById<Spinner>(R.id.spinnerFile)
//        spinner.adapter = adapter

//        findViewById<Button>(R.id.btnReadLog).setOnClickListener {
//            readLog()
//        }
    }

    private fun readLog() {
        // val log = readLog(spinner.selectedItem.toString())
        val log = readLog("tail -n 30 /ota/FOTA/Log/fotaMaster.log")

        try {
            val vw = findViewById<WebView>(R.id.webView)
            vw.settings.loadWithOverviewMode = true
//            vw.settings.useWideViewPort = true
            vw.loadData(log.toString(), "text/html", "UTF-8")
//            vw.setWebViewClient(object : WebViewClient() {
//                override fun onPageFinished(view: WebView, url: String) {
//                    view.pageDown(true)
//                }
//            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.about_dialog)
                    .show()
                    .apply {
                        findViewById<TextView>(android.R.id.message)
                            ?.movementMethod = LinkMovementMethod.getInstance()
                    }
                true
            }
            R.id.action_quit -> {
                finish()
                true
            }
            R.id.action_reload -> {
                readLog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun readLog(command: String) : StringBuilder{
        val process = Runtime.getRuntime().exec(command)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        val log = StringBuilder("<html>" +
                "  <head><style>" +
//                "div { white-space: pre; }" + //no wrap text
                "</style></head>" +
                "<body><p>fotaMaster.log<p><div>")
        while (bufferedReader.readLine().also {
                if(it != null) {
                    if(it.contains("\"name\":\"BX1E")) {
                        log.append("<font color=\"green\">$it</font><br />")
                    } else {
                        log.append("$it<br />")
                    }
                }
            } != null) {
        }
        log.append("</div></body></html>")
        return log

    }

    private fun requestForStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val read = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (read) {
                    Toast.makeText(
                        this@MainActivity,
                        "Storage Permissions Granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Storage Permissions Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}