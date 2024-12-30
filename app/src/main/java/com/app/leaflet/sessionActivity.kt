package com.app.leaflet

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class sessionActivity : AppCompatActivity() {

    private lateinit var sessionDay : List<CheckBox>
    private lateinit var sessionTime : TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.session_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val group_Id = intent.getIntExtra("GroupID", 0)
        if (group_Id == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404 ", Toast.LENGTH_SHORT).show()
            finish()
        }

        sessionTime = findViewById(R.id.timePicker)
        sessionDay = listOf<CheckBox>(
            findViewById(R.id.sunday),
            findViewById(R.id.monday),
            findViewById(R.id.tuesday),
            findViewById(R.id.wednesday),
            findViewById(R.id.thursday),
            findViewById(R.id.saturday)
        )

        val planerTime = intent.getStringExtra("PlanerTime")

        if (planerTime != null) {
            setTime(sessionTime, planerTime)
        }

        (findViewById<TextView>(R.id.tvBack)).setOnClickListener {
            finish()
        }

        (findViewById<Button>(R.id.confirmButton)).setOnClickListener {
            confirm()
        }
    }

    private fun checkFields(): Boolean{
        var checked = false

        for (checkBox in sessionDay){
            if(checkBox.isChecked){
                checked = true
                break
            }
        }
        if(!checked){
            Toast.makeText(this@sessionActivity, "Please check at least one checkbox", Toast.LENGTH_LONG).show()
            return false
        }

        if(sessionTime.hour >= 18 || sessionTime.hour <= 7 ){
            Toast.makeText(this@sessionActivity, "the time entered is not realistic", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun setTime(timePicker: TimePicker, time: String) {
        val parts = time.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toIntOrNull() ?: return
            val minute = parts[1].toIntOrNull() ?: return

            timePicker.hour = hour
            timePicker.minute = minute
        } else {
            throw IllegalArgumentException("Time must be in the format HH:MM")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getTime(timePicker: TimePicker): String {
        val hour = timePicker.hour
        val minute = timePicker.minute
        return String.format("%02d:%02d", hour, minute)
    }

    private fun confirm() {
        if(checkFields()){
            val database = LeafLetLocalDatabase.getDatabase(this)
            val planerDao = database.planerDao()

            val sId = intent.getIntExtra("PlanerID", 0)
            val sTime = getTime(sessionTime)
            val gId = intent.getIntExtra("GroupID", 0)


            if (sId == 0){
                val planersList = mutableListOf<UnivPlaner>()
                for (checkbox in sessionDay){
                    if (checkbox.isChecked){
                        val addedPlan = UnivPlaner(sId, checkbox.text.toString(), sTime, gId)
                        planersList.add(addedPlan)
                    }
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        planerDao.insertPlaners(planersList)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "Session Plan added successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: SQLiteConstraintException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "This Session plan already exists. Please enter unique details.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e : Exception){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val planerDay = intent.getStringExtra("PlanerDay").toString()

                    val planer = UnivPlaner(sId, planerDay, sTime, gId)
                    try {
                        planerDao.updatePlaner(planer)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "Session Plan updated successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: SQLiteConstraintException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "This Session plan already exists. Please enter unique details.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e : Exception){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@sessionActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
