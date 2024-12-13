package com.app.leaflet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KFunction1

class UnivClassRecyclerAdapter : RecyclerView.Adapter<UnivClassRecyclerAdapter.UnivClassViewHolder>() {
    private val classes = mutableListOf<UnivClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return UnivClassViewHolder(view, ::updateList)
    }

    override fun onBindViewHolder(holder: UnivClassViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount() = classes.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newClasses: List<UnivClass>) {
        classes.clear()
        classes.addAll(newClasses)
        notifyDataSetChanged()
    }

    class UnivClassViewHolder(itemView: View, updateList: KFunction1<List<UnivClass>, Unit>) : RecyclerView.ViewHolder(itemView) {

        private val tvNameClass: TextView = itemView.findViewById(R.id.tvNameClass)
        private val tvClassSP: TextView = itemView.findViewById(R.id.tvClassSP)
        private val tvClassLevel: TextView = itemView.findViewById(R.id.tvClassLevel)
        private val tvAdd: TextView = itemView.findViewById(R.id.buttonAdd)
        private val tvEdit: TextView = itemView.findViewById(R.id.buttonEdit)
        private val tvDelete: TextView = itemView.findViewById(R.id.buttonDelete)
        private val infoHolder: LinearLayout = itemView.findViewById(R.id.class_info_holder)
        private val buttonHolder: LinearLayout = itemView.findViewById(R.id.typeButtons)
        private val tdHolder: LinearLayout = itemView.findViewById(R.id.TDholder)
        private val tpHolder: LinearLayout = itemView.findViewById(R.id.TPholder)
        private val recyclerViewGroupTD: RecyclerView = itemView.findViewById(R.id.recyclerViewGroupTD)
        private val recyclerViewGroupTP: RecyclerView = itemView.findViewById(R.id.recyclerViewGroupTP)

        val updateFunc = updateList

        fun bind(univClass: UnivClass) {

            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val univClassDao = database.univClassDao()
            val univGroupDao = database.groupDao()

            tvNameClass.text = univClass.name
            tvClassSP.text = univClass.specialty
            tvClassLevel.text = univClass.level

            tvAdd.setOnClickListener {
                val intent = Intent(itemView.context, groupActivity::class.java)
                intent.putExtra("ClassID", univClass.id)
                itemView.context.startActivity(intent)
            }

            tvEdit.setOnClickListener {
                val intent = Intent(itemView.context, NewClass::class.java)
                intent.putExtra("ClassID", univClass.id)
                intent.putExtra("ClassName", univClass.name)
                intent.putExtra("ClassSp", univClass.specialty)
                intent.putExtra("ClassLevel", univClass.level)
                intent.putExtra("ClassYear", univClass.year)

                itemView.context.startActivity(intent)
            }

            tvDelete.setOnClickListener {
                showDeleteConfirmationDialog(univClass.name){

                    val context = itemView.context
                    if (context is LifecycleOwner) {
                        context.lifecycleScope.launch(Dispatchers.IO) {
                            univClassDao.deleteClass(univClass)
                            val updatedClasses = univClassDao.getClassByYear(univClass.year)
                            withContext(Dispatchers.Main) {
                                updateFunc(updatedClasses)
                            }
                        }
                    } else {
                        throw IllegalStateException("Context is not a LifecycleOwner")
                    }
                }
            }

            val groupAdapterTD = UnivGroupRecyclerAdapter()
            val groupAdapterTP = UnivGroupRecyclerAdapter()


            val context = itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch(Dispatchers.IO) {
                    val fetchedGroups = univGroupDao.getGroupByClassId(univClass.id)
                    withContext(Dispatchers.Main) {
                        groupAdapterTD.updateList(fetchedGroups.filter { group -> group.type == "TD" })
                        groupAdapterTP.updateList(fetchedGroups.filter { group -> group.type == "TP" })
                    }
                }
            } else {
                throw IllegalStateException("Context is not a LifecycleOwner")
            }


            recyclerViewGroupTD.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = groupAdapterTD
            }

            recyclerViewGroupTP.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = groupAdapterTP
            }

            buttonHolder.visibility = View.GONE
            infoHolder.setOnClickListener {
                buttonHolder.visibility =
                    if (buttonHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            recyclerViewGroupTD.visibility = View.GONE
            tdHolder.setOnClickListener {
                recyclerViewGroupTD. visibility =
                    if (recyclerViewGroupTD.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            recyclerViewGroupTP.visibility = View.GONE
            tpHolder.setOnClickListener {
                recyclerViewGroupTP. visibility =
                    if (recyclerViewGroupTP.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

        private fun showDeleteConfirmationDialog(className: String, onConfirm: () -> Unit) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Delete Class")
            builder.setMessage("Are you sure you want to delete the class: \"$className\"?")

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



