package com.app.leaflet
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class studentsfromExcel : AppCompatActivity() {

    private lateinit var btnSelectFile: Button
    private lateinit var tvFileContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studentfrom_excel)

        btnSelectFile = findViewById(R.id.btnSelectFile)
        tvFileContent = findViewById(R.id.tvFileContent)

        btnSelectFile.setOnClickListener {
            selectExcelFile()
        }
    }

    private fun selectExcelFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            try {
                uri?.let {
                    val inputStream: InputStream? = contentResolver.openInputStream(it)
                    val workbook = WorkbookFactory.create(inputStream)
                    val sheet = workbook.getSheetAt(0)
                    val rows = StringBuilder()

                    // Iterate through rows and cells
                    for (row in sheet) {
                        for (cell in row) {
                            rows.append(cell.toString()).append("\t")
                        }
                        rows.append("\n")
                    }

                    tvFileContent.text = rows.toString()
                    inputStream?.close()
                }
            } catch (e: Exception) {
                tvFileContent.text = "Error reading file: ${e.message}"
            }
        }
    }
}
