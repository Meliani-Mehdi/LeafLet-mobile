package com.app.leaflet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UnivGroupRecyclerAdapter : RecyclerView.Adapter<UnivGroupRecyclerAdapter.UnivGroupViewHolder>() {
    private val groups = mutableListOf<UnivGroup>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return UnivGroupViewHolder(view)
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

    class UnivGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvGroupName: TextView = itemView.findViewById(R.id.tvNameGroup)
        private val tvTypeGroup: TextView = itemView.findViewById(R.id.tvTypeGroup)
        private val tvEditGroup: TextView = itemView.findViewById(R.id.tvEditGroup)
        private val tvDeleteGroup: TextView = itemView.findViewById(R.id.tvDeleteGroup)

        fun bind(univGroup: UnivGroup) {
            tvGroupName.text = univGroup.name
            tvTypeGroup.text = univGroup.type

        }
    }
}
