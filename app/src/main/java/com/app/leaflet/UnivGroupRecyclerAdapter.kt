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

class UnivGroupRecyclerAdapter : RecyclerView.Adapter<UnivGroupRecyclerAdapter.UnivGroupViewHolder>() {
    private val groups = mutableListOf<UnivGroup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return UnivGroupViewHolder(view, ::updateList)
    }

    override fun onBindViewHolder(holder: UnivGroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount() = groups.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newGroups: List<UnivGroup>) {
        groups.clear()
        groups.addAll(newGroups)
        notifyDataSetChanged()
    }

    class UnivGroupViewHolder(itemView: View, updateList: KFunction1<List<UnivGroup>, Unit>) : RecyclerView.ViewHolder(itemView) {

        private val tvGroupName: TextView = itemView.findViewById(R.id.tvNameGroup)
        private val tvEditGroup: TextView = itemView.findViewById(R.id.tvEditGroup)
        private val tvDeleteGroup: TextView = itemView.findViewById(R.id.tvDeleteGroup)
        private val itemHolder: ConstraintLayout = itemView.findViewById(R.id.group_holder)

        val updateFunc = updateList

        fun bind(univGroup: UnivGroup) {

            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val univGroupDao = database.groupDao()

            tvGroupName.text = univGroup.name

            tvEditGroup.setOnClickListener {
                val intent = Intent(itemView.context, groupActivity::class.java)
                intent.putExtra("GroupID", univGroup.id)
                intent.putExtra("GroupName", univGroup.name)
                intent.putExtra("GroupType", univGroup.type)
                intent.putExtra("ClassID", univGroup.univClassId)

                itemView.context.startActivity(intent)
            }

            tvDeleteGroup.setOnClickListener{
                showDeleteConfirmationDialog(univGroup.name){
                    val context = itemView.context
                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            univGroupDao.deleteGroup(univGroup)
                            if (univGroup.univClassId is Int){
                                val updatedGroups = univGroupDao.getGroupByClassId(univGroup.univClassId)
                                withContext(Dispatchers.Main) {
                                    updateFunc(updatedGroups)
                                }
                            }
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }
            }

            itemHolder.setOnClickListener{
                val intent = Intent(itemView.context, StudentViewerActivity::class.java)
                intent.putExtra("GroupID", univGroup.id)

                itemView.context.startActivity(intent)
            }

        }

        private fun showDeleteConfirmationDialog(groupName: String, onConfirm: () -> Unit) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Delete Class")
            builder.setMessage("Are you sure you want to delete the group: \"$groupName\"?")

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
