package com.app.leaflet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.lifecycleScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import java.io.InputStream;

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
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(data.data!!)
                inputStream?.let {
                    val workbook = WorkbookFactory.create(it)
                    val sheet: Sheet = workbook.getSheetAt(0)

                    // Validate the format of the Excel file
                    val headerRow: Row? = sheet.getRow(0)
                    if (headerRow == null || !isValidHeader(headerRow)) {
                        tvFileContent.text = "Invalid file format. The file must have columns: First Name, Last Name, Group ID."
                        inputStream.close()
                        return
                    }
   // -----------------------------------------here-----------------------------------------------
                    val database = LeafLetLocalDatabase.getDatabase(this)
                    val studentDao = database.studentDao()

                    lifecycleScope.launch(Dispatchers.IO) {
                        // Iterate through rows and insert students into the database
                        for (i in 1..sheet.lastRowNum) { // Skip the header row
                            val row: Row? = sheet.getRow(i)
                            row?.let {
                                val firstName = row.getCell(0)?.toString()?.trim() ?: ""
                                val lastName = row.getCell(1)?.toString()?.trim() ?: ""
                                val groupId = row.getCell(2)?.toString()?.trim() ?: ""

                                if (firstName.isNotEmpty() && lastName.isNotEmpty() && groupId.isNotEmpty()) {
//                                    val newStudent = UnivStudent(
//                                        id = 0, // Assuming ID is auto-generated
//                                        firstName = firstName,
//                                        lastName = lastName,
//                                        groupId = groupId
//                                    )
//                                    studentDao.insertStudent(newStudent)
                                }
                            }
                        }
                    }

                    val rows = StringBuilder()
                    for (i in 1..sheet.lastRowNum) { // Skip the header row
                        val row: Row? = sheet.getRow(i)
                        row?.let {
                            for (j in 0 until row.lastCellNum) {
                                rows.append(row.getCell(j)).append("\t")
                            }
                            rows.append("\n")
                        }
                    }

                    tvFileContent.text = rows.toString()
                    inputStream.close()
                }
            } catch (e: Exception) {
                tvFileContent.text = "Error reading file: ${e.message}"
            }
        }
    }

    private fun isValidHeader(headerRow: Row): Boolean {
        if (headerRow.lastCellNum < 3) return false // Ensure at least 3 columns

        val firstNameHeader = headerRow.getCell(0)?.toString()?.trim()?.lowercase() ?: ""
        val lastNameHeader = headerRow.getCell(1)?.toString()?.trim()?.lowercase() ?: ""
        val groupIdHeader = headerRow.getCell(2)?.toString()?.trim()?.lowercase() ?: ""

        return firstNameHeader == "first name" &&
                lastNameHeader == "last name" &&
                groupIdHeader == "group id"
    }
}
