package com.app.leaflet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KFunction1

class UnivStudentRecyclerAdapter : RecyclerView.Adapter<UnivStudentRecyclerAdapter.UnivStudentViewHolder>() {
    private val students = mutableListOf<UnivStudent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivStudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return UnivStudentViewHolder(view, ::updateList)
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun onBindViewHolder(holder: UnivStudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newStudent: List<UnivStudent>) {
        students.clear()
        students.addAll(newStudent)
        notifyDataSetChanged()
    }

    class UnivStudentViewHolder(itemView: View, updateList: KFunction1<List<UnivStudent>, Unit>) : RecyclerView.ViewHolder(itemView) {

        private val tvFirstNameStudent = itemView.findViewById<TextView>(R.id.tvFirstNameStudent)
        private val tvLastNameStudent = itemView.findViewById<TextView>(R.id.tvLastNameStudent)
        private val tvEditStudent = itemView.findViewById<TextView>(R.id.tvEditStudent)
        private val tvDeleteStudent = itemView.findViewById<TextView>(R.id.tvDeleteStudent)

        val updateFunc = updateList

        fun bind(univStudent: UnivStudent) {
            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val univStudentdDao = database.studentDao()

            tvFirstNameStudent.text = univStudent.firstName
            tvLastNameStudent.text = univStudent.lastName

            tvEditStudent.setOnClickListener {
                val intent = Intent(itemView.context, StudentActivity::class.java)
                intent.putExtra("StudentID", univStudent.id)
                intent.putExtra("StudentFirstName", univStudent.firstName)
                intent.putExtra("StudentLastName", univStudent.lastName)
                intent.putExtra("GroupID", univStudent.univGroupId)

                itemView.context.startActivity(intent)
            }

            tvDeleteStudent.setOnClickListener{
                showDeleteConfirmationDialog("${univStudent.firstName} ${univStudent.lastName}"){
                    val context = itemView.context
                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            univStudentdDao.deleteStudent(univStudent)
                            if (univStudent.univGroupId is Int){
                                val updatedStudents = univStudentdDao.getStudentByGroupId(univStudent.univGroupId)
                                withContext(Dispatchers.Main) {
                                    updateFunc(updatedStudents)
                                }
                            }
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }
            }

        }

        private fun showDeleteConfirmationDialog(studentName: String, onConfirm: () -> Unit) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Delete Class")
            builder.setMessage("Are you sure you want to delete the Student: \"$studentName\"?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}