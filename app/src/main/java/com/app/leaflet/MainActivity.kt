package com.app.leaflet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //test 4:55
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Main_window::class.java))
            finish()
        }, 2500)
    }
}

//package com.app.leaflet
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import org.apache.poi.ss.usermodel.WorkbookFactory
//import java.io.InputStream
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var btnSelectFile: Button
//    private lateinit var tvFileContent: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.studentfrom_excel)
//
//        btnSelectFile = findViewById(R.id.btnSelectFile)
//        tvFileContent = findViewById(R.id.tvFileContent)
//
//        btnSelectFile.setOnClickListener {
//            selectExcelFile()
//        }
//    }
//
//    private fun selectExcelFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//        startActivityForResult(intent, 1)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
//            val uri = data.data
//            try {
//                uri?.let {
//                    val inputStream: InputStream? = contentResolver.openInputStream(it)
//                    val workbook = WorkbookFactory.create(inputStream)
//                    val sheet = workbook.getSheetAt(0)
//                    val rows = StringBuilder()
//
//                    // Iterate through rows and cells
//                    for (row in sheet) {
//                        for (cell in row) {
//                            rows.append(cell.toString()).append("\t")
//                        }
//                        rows.append("\n")
//                    }
//
//                    tvFileContent.text = rows.toString()
//                    inputStream?.close()
//                }
//            } catch (e: Exception) {
//                tvFileContent.text = "Error reading file: ${e.message}"
//            }
//        }
//    }
//}
