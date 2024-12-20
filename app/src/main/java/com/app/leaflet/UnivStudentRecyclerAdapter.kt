package com.app.leaflet

import android.annotation.SuppressLint
import android.app.AlertDialog
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

        val updateFunc = updateList

        fun bind(univStudent: UnivStudent) {
            tvFirstNameStudent.text = univStudent.firstName
            tvLastNameStudent.text = univStudent.lastName
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