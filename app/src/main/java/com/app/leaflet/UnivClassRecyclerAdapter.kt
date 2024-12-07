package com.app.leaflet

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnivClassRecyclerAdapter : RecyclerView.Adapter<UnivClassRecyclerAdapter.UnivClassViewHolder>() {
    private val classes = mutableListOf<UnivClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivClassViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return UnivClassViewHolder(view)
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

    class UnivClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNameClass: TextView = itemView.findViewById(R.id.tvNameClass)
        private val tvClassSP: TextView = itemView.findViewById(R.id.tvClassSP)
        private val tvClassLevel: TextView = itemView.findViewById(R.id.tvClassLevel)
        private val tvClassYear: TextView = itemView.findViewById(R.id.tvClassYear)
        private val tvAdd: TextView = itemView.findViewById(R.id.buttonAdd)
        private val tvEdit: TextView = itemView.findViewById(R.id.buttonEdit)
        private val tvDelete: TextView = itemView.findViewById(R.id.buttonDelete)
        private val recyclerViewGroup: RecyclerView = itemView.findViewById(R.id.recyclerViewGroup)

        fun bind(univClass: UnivClass) {

            val database = LeafLetLocalDatabase.getDatabase(itemView.context)
            val univGroupDao = database.groupDao()

            tvNameClass.text = univClass.name
            tvClassSP.text = univClass.specialty
            tvClassLevel.text = univClass.level
            tvClassYear.text = univClass.year

            tvAdd.setOnClickListener {
                val intent = Intent(itemView.context, groupActivity::class.java)
                intent.putExtra("ClassID", univClass.id)
                itemView.context.startActivity(intent)
            }

            val groupAdapter = UnivGroupRecyclerAdapter()

            val context = itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch(Dispatchers.IO) {
                    val fetchedGroups = univGroupDao.getGroupByClassId(univClass.id)
                    withContext(Dispatchers.Main) {
                        groupAdapter.updateList(fetchedGroups)
                    }
                }
            } else {
                throw IllegalStateException("Context is not a LifecycleOwner")
            }


            recyclerViewGroup.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = groupAdapter
            }

            recyclerViewGroup.visibility = View.GONE
            tvNameClass.setOnClickListener {
                recyclerViewGroup.visibility =
                    if (recyclerViewGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

    }
}



