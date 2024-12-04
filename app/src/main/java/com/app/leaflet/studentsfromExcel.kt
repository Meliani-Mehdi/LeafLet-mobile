package com.app.leaflet;

import android.content.Context;
import androidx.lifecycle.lifecycleScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.withContext;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row;
import java.io.File;
import java.io.FileInputStream;

class studentsfromExcel  {

    suspend fun StudentsFromExcel(group_id: Int, file_path: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Verify if the file is an Excel file
                val file = File(file_path)
                if (!file.exists() || !file.extension.equals("xlsx", ignoreCase = true)) {
                    return@withContext false
                }

                val inputStream = FileInputStream(file)
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)

                // Validate the format of the Excel file
                val headerRow: Row? = sheet.getRow(0)
                if (headerRow == null || !isValidHeader(headerRow)) {
                    inputStream.close()
                    return@withContext false
                }

                val database = LeafLetLocalDatabase.getDatabase(context)
                val studentDao = database.studentDao()

                // Iterate through rows and insert students into the database
                for (i in 1..sheet.lastRowNum) { // Skip the header row
                    val row: Row? = sheet.getRow(i)
                    row?.let {
                        val firstName = row.getCell(0)?.toString()?.trim() ?: ""
                        val lastName = row.getCell(1)?.toString()?.trim() ?: ""

//                        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
//                            val newStudent = UnivStudent(
//                                id = 0, // Assuming ID is auto-generated
//                                firstName = firstName,
//                                lastName = lastName,
//                                groupId = group_id.toString()
//                            )
//                            studentDao.insertStudent(newStudent)
//                        }
                    }
                }

                inputStream.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun isValidHeader(headerRow: Row): Boolean {
        if (headerRow.lastCellNum < 2) return false // Ensure at least 2 columns

        val firstNameHeader = headerRow.getCell(0)?.toString()?.trim()?.lowercase() ?: ""
        val lastNameHeader = headerRow.getCell(1)?.toString()?.trim()?.lowercase() ?: ""

        return firstNameHeader == "first name" &&
                lastNameHeader == "last name"
    }
}
