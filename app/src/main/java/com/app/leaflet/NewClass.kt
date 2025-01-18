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
        className = findViewById(R.id.class_name)
        classSpecialty = findViewById(R.id.class_speciality)
        classLevel = findViewById(R.id.class_level)
        classYearSpinner = findViewById(R.id.class_year)

        //this will fill the spinner with the wanted years
        var currentYear = android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.YEAR)
        val currentMonth = android.icu.util.Calendar.getInstance().get(android.icu.util.Calendar.MONTH)
        if(currentMonth <= 7) currentYear -= 1

        val years = mutableListOf<String>()
        for (year in ( 2015 )..(currentYear + 3)) {
            val yearRange = "$year-${year + 1}"
            years.add(yearRange)
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_item, years)

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        classYearSpinner.adapter = adapter

        val targetPosition = years.indexOf("$currentYear-${currentYear + 1}")
        if (targetPosition >= 0) {
            classYearSpinner.setSelection(targetPosition)
        }

        val cName = intent.getStringExtra("ClassName")
        val cSp = intent.getStringExtra("ClassSp")
        val cLevel = intent.getStringExtra("ClassLevel")
        val cYear = intent.getStringExtra("ClassYear")

        className.setText(cName)
        classSpecialty.setText(cSp)
        classLevel.setText(cLevel)

        val position = years.indexOf(cYear)
        if (position >= 0) {
            classYearSpinner.setSelection(position)
        }

    }

    fun go_back(v: View) {
        finish()
    }

    private fun checkFields(): Boolean {
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

            val cId = intent.getIntExtra("ClassID", 0)
            val name = className.text.trim().toString()
            val specialty = classSpecialty.text.trim().toString()
            val level = classLevel.text.trim().toString()
            val year = classYearSpinner.selectedItem.toString()

            val univClass = UnivClass(cId, name, specialty, level, year)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    univClassDao.insertClass(univClass)
                    withContext(Dispatchers.Main) {
                        if(cId == 0){
                            Toast.makeText(this@NewClass, "Class added successfully!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this@NewClass, "Class updated successfully!", Toast.LENGTH_SHORT).show()
                        }
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