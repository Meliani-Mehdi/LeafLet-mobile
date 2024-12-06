package com.app.leaflet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

            tvNameClass.setOnClickListener {
                recyclerViewGroup.visibility = View.VISIBLE

            }
        }

    }
}



