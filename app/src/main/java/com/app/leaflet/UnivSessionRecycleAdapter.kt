package com.app.leaflet

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class UnivSessionRecycleAdapter : RecyclerView.Adapter<UnivSessionRecycleAdapter.UnivSessionViewHolder>() {

    private val sessionPlans = mutableListOf<SessionPlan>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSessionPlans: List<SessionPlan>) {
        sessionPlans.clear()
        sessionPlans.addAll(newSessionPlans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnivSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session2, parent, false)
        return UnivSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnivSessionViewHolder, position: Int) {
        val sessionPlan = sessionPlans[position]
        holder.bind(sessionPlan)
    }

    override fun getItemCount(): Int = sessionPlans.size

    class UnivSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val className: TextView = itemView.findViewById(R.id.classNameSession)
        private val specialization: TextView = itemView.findViewById(R.id.spClassSession)
        private val level: TextView = itemView.findViewById(R.id.levelClassSession)
        private val groupName: TextView = itemView.findViewById(R.id.groupNameSession)
        private val day: TextView = itemView.findViewById(R.id.tvDaySession2)
        private val time: TextView = itemView.findViewById(R.id.tvTimeSession2)
        private val holder: ConstraintLayout = itemView.findViewById(R.id.session_plan_holder)

        @SuppressLint("SetTextI18n")
        fun bind(sessionPlan: SessionPlan) {
            className.text = sessionPlan.className ?: "Unknown Class"
            specialization.text = "Specialization: ${sessionPlan.classSpecialty ?: "N/A"}"
            level.text = "Level: ${sessionPlan.classLevel ?: "N/A"}"
            groupName.text = "${sessionPlan.groupName ?: "N/A"}, ${sessionPlan.groupType ?: "N/A"}"
            day.text = sessionPlan.day
            time.text = sessionPlan.time

            holder.setOnClickListener {
                val intentSession = Intent(itemView.context, sessionPlanerVeiwActivity::class.java).apply {
                    putExtra("GroupID", sessionPlan.groupId)
                    putExtra("PlanerID", sessionPlan.planerId)
                    putExtra("ClassName", sessionPlan.className ?: "Unknown Class")
                    putExtra("Specialization", sessionPlan.classSpecialty ?: "N/A")
                    putExtra("Level", sessionPlan.classLevel ?: "N/A")
                    putExtra("GroupName", sessionPlan.groupName ?: "N/A")
                    putExtra("GroupType", sessionPlan.groupType ?: "N/A")
                    putExtra("Day", sessionPlan.day)
                    putExtra("Time", sessionPlan.time)
                }

                itemView.context.startActivity(intentSession)
            }
        }
    }
}
