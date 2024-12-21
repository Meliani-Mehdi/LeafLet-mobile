package com.app.leaflet

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentActivity : AppCompatActivity() {

    private lateinit var studentFirstName: EditText
    private lateinit var studentLastName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val groupId = intent.getIntExtra("GroupID", 0)
        if(groupId == 0){
            Toast.makeText(this, "An error has occurred, error code: 2404 ", Toast.LENGTH_SHORT).show()
            finish()
        }

        studentFirstName = findViewById(R.id.first_name)
        studentLastName = findViewById(R.id.last_name)

        val sFirstName = intent.getStringExtra("StudentFirstName")
        val sLastName = intent.getStringExtra("StudentLastName")

        studentFirstName.setText(sFirstName)
        studentLastName.setText(sLastName)

    }

    fun go_back(view: View) {
        finish()
    }

    private fun checkFields(): Boolean{
        if(studentFirstName.text.trim().isEmpty()){
            studentFirstName.error = "This field is required"
            return false
        }
        if(studentLastName.text.trim().isEmpty()){
            studentLastName.error = "This field is required"
            return false
        }
        return true
    }

    fun confirm(view: View) {
        if(checkFields()){
            val database = LeafLetLocalDatabase.getDatabase(this)
            val studentdao = database.studentDao()

            val sId = intent.getIntExtra("StudentID", 0)
            val firstName = studentFirstName.text.trim().toString()
            val lastName = studentLastName.text.trim().toString()
            val gId = intent.getIntExtra("GroupID", 0)

            val newStudent = UnivStudent(sId, firstName, lastName, gId)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val message : String
                    if(sId == 0) {
                        studentdao.insertStudent(newStudent)
                        message = "Student added successfully!"
                    }
                    else {
                        studentdao.updateStudent(newStudent)
                        message = "Student updated successfully!"
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StudentActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: SQLiteConstraintException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StudentActivity, "This Group already exists. Please enter unique details.", Toast.LENGTH_LONG).show()
                    }
                } catch (e : Exception){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StudentActivity, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

            }


        }
    }
}