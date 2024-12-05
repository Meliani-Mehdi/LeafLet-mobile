package com.app.leaflet

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NewClass : AppCompatActivity() {

    private lateinit var className: EditText
    private lateinit var classSpecialty: EditText
    private lateinit var classLevel: EditText
    private lateinit var classYearSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_class)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        className = findViewById<EditText>(R.id.class_name)
        classSpecialty = findViewById<EditText>(R.id.class_speciality)
        classLevel = findViewById<EditText>(R.id.class_level)
        classYearSpinner = findViewById<Spinner>(R.id.class_year)


        //this will fii the spinner with the wanted years
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val years = mutableListOf("Select Year")
        for (year in (currentYear - 2)..(currentYear + 5)) {
            val yearRange = "$year-${year + 1}"
            years.add(yearRange)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        classYearSpinner.adapter = adapter

    }

    fun go_back(v: View) {
        finish()
    }

    fun checkFields(): Boolean {
        if(className.text.trim().isEmpty()){
            className.error = "This field is required"
            return false
        }
        if(classSpecialty.text.trim().isEmpty()){
            classSpecialty.error = "This field is required"
            return false
        }
        if(classLevel.text.trim().isEmpty()){
            classLevel.error = "This field is required"
            return false
        }

        if (classYearSpinner.selectedItem == "Select Year") {
            Toast.makeText(this, "Please select a valid year", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    fun confirm(v: View) {
        if (checkFields()) {
            val database = LeafLetLocalDatabase.getDatabase(this)
            val univClassDao = database.univClassDao()

            val name = className.text.trim().toString()
            val specialty = classSpecialty.text.trim().toString()
            val level = classLevel.text.trim().toString()
            val year = classYearSpinner.selectedItem.toString()

            val univClass = UnivClass(id = 0, name, specialty, level, year)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    univClassDao.insertClass(univClass)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewClass, "Class added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: SQLiteConstraintException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewClass, "This class already exists. Please enter unique details.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NewClass, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



}