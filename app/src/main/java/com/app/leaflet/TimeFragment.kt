package com.app.leaflet

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [TimeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeFragment : Fragment() {
    private lateinit var toggleButtons: List<ToggleButton>
    private lateinit var recyclerViewSessionPlan: RecyclerView
    private lateinit var univSessionRecycleAdapter: UnivSessionRecycleAdapter

    private var sessionPlan: List<SessionPlan> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time, container, false)

        recyclerViewSessionPlan = view.findViewById(R.id.sessionPlanRC)

        toggleButtons = listOf(
            view.findViewById(R.id.tbNextSession),
            view.findViewById(R.id.tbSunday),
            view.findViewById(R.id.tbMonday),
            view.findViewById(R.id.tbTuesday),
            view.findViewById(R.id.tbWednesday),
            view.findViewById(R.id.tbThursday),
            view.findViewById(R.id.tbSaturday),
            view.findViewById(R.id.tbAllTime)
        )

        univSessionRecycleAdapter = UnivSessionRecycleAdapter()
        recyclerViewSessionPlan.adapter = univSessionRecycleAdapter
        recyclerViewSessionPlan.layoutManager = LinearLayoutManager(context)

        loadInitialData()

        setupToggleButtonLogic()

        return view
    }

    private fun setupToggleButtonLogic() {
        for (button in toggleButtons) {
            button.setOnClickListener { selectedButton ->
                toggleButtons.forEach { it.isChecked = false }
                (selectedButton as ToggleButton).isChecked = true

                val selectedDay = when (selectedButton.id) {
                    R.id.tbNextSession -> "Next Session"
                    R.id.tbSunday -> "Sunday"
                    R.id.tbMonday -> "Monday"
                    R.id.tbTuesday -> "Tuesday"
                    R.id.tbWednesday -> "Wednesday"
                    R.id.tbThursday -> "Thursday"
                    R.id.tbSaturday -> "Saturday"
                    R.id.tbAllTime -> "AllTime"
                    else -> null
                }

                filterRecyclerViewByDay(selectedDay)
            }
        }

        val nextSessionButton = view?.findViewById<ToggleButton>(R.id.tbNextSession)
        nextSessionButton?.isChecked = true
        filterRecyclerViewByDay("Next Session")
    }

    private fun filterRecyclerViewByDay(selectedDay: String?) {
        val currentTime = LocalTime.now()

        val oneHourBefore = currentTime.minusHours(1)
        val fifteenMinutesAfter = currentTime.plusMinutes(15)

        val filtered = when (selectedDay) {
            "AllTime" -> sessionPlan
            "Next Session" -> {
                    sessionPlan.filter { session ->
                        val sessionTime = LocalTime.parse(session.time, DateTimeFormatter.ofPattern("HH:mm"))
                        sessionTime.isAfter(oneHourBefore) && sessionTime.isBefore(fifteenMinutesAfter)
                    }
            }
            else -> sessionPlan.filter { it.day == selectedDay }
        }

        univSessionRecycleAdapter.updateData(filtered)
    }

    private fun loadInitialData() {
        val database = LeafLetLocalDatabase.getDatabase(requireContext())
        val univPlanerDao = database.planerDao()
        val TAG = "TimeFragment"
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        if(currentMonth <= 7) currentYear -= 1
        val selectedYear = "$currentYear-${currentYear + 1}"

        lifecycleScope.launch(Dispatchers.IO) {
            val sessionplaners = univPlanerDao.getSessionPlansByYear(selectedYear)

            withContext(Dispatchers.Main) {
                sessionPlan = sessionplaners
                filterRecyclerViewByDay("Next Session")
            }
        }
        Log.d(TAG, "${sessionPlan.size}")
    }
}
