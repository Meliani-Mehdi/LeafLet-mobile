package com.app.leaflet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KFunction1

class UnivPlanerRecyclerAdapter : RecyclerView.Adapter<UnivPlanerRecyclerAdapter.UnivPlanerViewHolder>() {
    private val planers = mutableListOf<UnivPlaner>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivPlanerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return UnivPlanerViewHolder(view, ::updateList)
    }

    override fun onBindViewHolder(holder: UnivPlanerViewHolder, position: Int) {
        holder.bind(planers[position])
    }

    override fun getItemCount() = planers.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newPlaners: List<UnivPlaner>) {
        planers.clear()
        planers.addAll(newPlaners)
        notifyDataSetChanged()
    }

    class UnivPlanerViewHolder(itemView: View, updateList: KFunction1<List<UnivPlaner>, Unit>) : RecyclerView.ViewHolder(itemView) {

        private val tvDaySession: TextView = itemView.findViewById(R.id.tvDaySession)
        private val tvTimeSession: TextView = itemView.findViewById(R.id.tvTimeSession)
        private val tvEditSession: TextView = itemView.findViewById(R.id.tvEditSession)
        private val tvDeleteSession: TextView = itemView.findViewById(R.id.tvDeleteSession)

        val updateFunc = updateList

        fun bind(univPlaner: UnivPlaner) {

            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val univPlanerDao = database.planerDao()

            tvDaySession.text = univPlaner.day
            tvTimeSession.text = univPlaner.time

            tvEditSession.setOnClickListener {
                val intent = Intent(itemView.context, sessionActivity::class.java)
                intent.putExtra("PlanerID", univPlaner.id)
                intent.putExtra("PlanerDay", univPlaner.day)
                intent.putExtra("PlanerTime", univPlaner.time)
                intent.putExtra("GroupID", univPlaner.univGroupId)

                itemView.context.startActivity(intent)
            }

            tvDeleteSession.setOnClickListener{
                showDeleteConfirmationDialog("${univPlaner.day} ${univPlaner.time}"){
                    val context = itemView.context
                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            univPlanerDao.deletePlaner(univPlaner)
                            if (univPlaner.univGroupId is Int){
                                val updatedPlaners = univPlanerDao.getPlanerByGroupId(univPlaner.univGroupId)
                                withContext(Dispatchers.Main) {
                                    updateFunc(updatedPlaners)
                                }
                            }
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }
            }

        }

        private fun showDeleteConfirmationDialog(deleteContext: String, onConfirm: () -> Unit) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Delete Class")
            builder.setMessage("Are you sure you want to delete the session which is in: \"$deleteContext\"?")

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
