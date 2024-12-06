package com.app.leaflet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    fun updateList(newClasses: List<UnivClass>) {
        classes.clear()
        classes.addAll(newClasses)
        notifyDataSetChanged()
    }

    class UnivClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNameClass: TextView = itemView.findViewById(R.id.tvNameClass)
        private val tvEdit: TextView = itemView.findViewById(R.id.tvEdit)
        private val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)
        private val recyclerViewGroup: RecyclerView = itemView.findViewById(R.id.recyclerViewGroup)


        fun bind(univClass: UnivClass) {

            tvNameClass.text = univClass.name
            tvEdit.text = "Edit"
            tvDelete.text = "Delete"

            val groupAdapter = UnivGroupRecyclerAdapter()

            // Ajouter des données fictives au RecyclerView
            val groupes = mutableListOf(
                UnivGroup(13455432, "group 1", "TD", 1345543),
                UnivGroup(13455442, "group 2", "TD",  1345544),
                UnivGroup(13455452, "group 3", "TP", 1345545)
            )

            groupAdapter.updateList(groupes)

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



