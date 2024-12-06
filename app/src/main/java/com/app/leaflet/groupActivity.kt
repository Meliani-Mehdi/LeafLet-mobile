package com.app.leaflet

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

class groupActivity : AppCompatActivity() {

    private lateinit var groupName: EditText
    private lateinit var groupType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        groupName = findViewById<EditText>(R.id.groupName)
        groupType = findViewById<Spinner>(R.id.groupType)

        val group_types = listOf("TD", "TP")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, group_types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        groupType.adapter = adapter


    }

    fun go_back(v: View){
        finish()
    }

    fun checkFields(): Boolean {
        if(groupName.text.trim().isEmpty()){
            groupName.error = "This field is required"
            return false
        }

        if(groupType.selectedItem == null || groupType.selectedItem.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a valid Type", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun confirm(v:View){

    }

}