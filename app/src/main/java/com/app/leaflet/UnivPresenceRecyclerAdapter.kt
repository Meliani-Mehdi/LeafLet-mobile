package com.app.leaflet

import android.annotation.SuppressLint
import android.app.AlertDialog
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

class UnivPresenceRecyclerAdapter : RecyclerView.Adapter<UnivPresenceRecyclerAdapter.UnivPresenceViewHolder>() {
    private val presences = mutableListOf<UnivSession>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(session: List<UnivSession>) {
        presences.clear()
        presences.addAll(session)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivPresenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_presence, parent, false)
        return UnivPresenceViewHolder(view, ::updateList)
    }

    override fun onBindViewHolder(holder: UnivPresenceViewHolder, position: Int) {
        holder.bind(presences[position])
    }

    override fun getItemCount() = presences.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newPresences: List<UnivSession>) {
        presences.clear()
        presences.addAll(newPresences)
        notifyDataSetChanged()
    }

    class UnivPresenceViewHolder(itemView: View, updateList: KFunction1<List<UnivSession>, Unit>) : RecyclerView.ViewHolder(itemView) {

        private val dateTextView: TextView = itemView.findViewById(R.id.dateTvP)
        private val deleteButton: TextView = itemView.findViewById(R.id.tvDeleteSession)

        private val updateFunc = updateList

        fun bind(sessionPresence: UnivSession) {

            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val presenceDao = database.sessionDao()

            dateTextView.text = sessionPresence.date

            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(sessionPresence.date) {
                    val context = itemView.context
                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            presenceDao.deleteSession(sessionPresence)
                            if (sessionPresence.univPlanerId is Int) {
                                val updatedPresences = presenceDao.getSessionByPlanerId(sessionPresence.univPlanerId)
                                withContext(Dispatchers.Main) {
                                    updateFunc(updatedPresences)
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
            builder.setTitle("Delete Presence")
            builder.setMessage("Are you sure you want to delete the presence on \"$deleteContext\"?")

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

