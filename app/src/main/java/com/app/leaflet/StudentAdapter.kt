package com.app.leaflet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentAdapter : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private val students = mutableListOf<UnivPresence>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newStudents: List<UnivPresence>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prtudent, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount() = students.size

    fun getPrCount() = (students.filter { it.present =="Present" }).size
    fun getAbCount() = (students.filter { it.present =="Absent" }).size
    fun getAbjCount() = (students.filter { it.present =="ABJ" }).size

    class StudentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val firstNameTextView: TextView = itemView.findViewById(R.id.textView5)
        private val lastNameTextView: TextView = itemView.findViewById(R.id.textView6)
        private val statusSpinner: Spinner = itemView.findViewById(R.id.spinner2)
        private val commentEditText: EditText = itemView.findViewById(R.id.editTextText2)

        private val spinnerOptions = listOf("", "Present", "Absent", "ABJ")

        fun bind(student: UnivPresence) {
            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val presenceDao = database.presenceDao()
            val studentDao = database.studentDao()

            val context = itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch(Dispatchers.IO) {
                    val studentData = student.univStudentId?.let { studentDao.getStudentById(it) }
                    withContext(Dispatchers.Main){
                        if (studentData != null) {
                            firstNameTextView.text = studentData.firstName
                            lastNameTextView.text = studentData.lastName
                        }
                    }
                }
            } else {
                throw IllegalStateException("Context is not a LifecycleOwner")
            }
            
            // Setup spinner with predefined options
            val spinnerAdapter = ArrayAdapter(
                itemView.context,
                R.layout.spinner_item_black,
                spinnerOptions
            )
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_black)
            statusSpinner.adapter = spinnerAdapter

            // Set current selection
            val spinnerPosition = spinnerOptions.indexOf(student.present)
            if (spinnerPosition >= 0) {
                statusSpinner.setSelection(spinnerPosition)
            }

            // Handle spinner selection changes
            statusSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    student.present = spinnerOptions[position]

                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            presenceDao.updatePresence(student)
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                    // No action needed
                }
            })

            // Set current comment
            commentEditText.setText(student.comment)

            // Update the comment on focus change
            commentEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    student.comment = commentEditText.text.toString()

                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            presenceDao.updatePresence(student)
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }
            }
        }

    }
}