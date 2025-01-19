package com.app.leaflet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

private lateinit var univPresenceRecyclerAdapter: UnivPresenceRecyclerAdapter
private lateinit var recyclerView: RecyclerView

class sessionPlanerVeiwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session_planer_veiw)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val groupId = intent.getIntExtra("GroupID", 0)
        if (groupId == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404", Toast.LENGTH_SHORT).show()
            finish()
        }

        val planerId = intent.getIntExtra("PlanerID", 0)
        if (planerId == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404", Toast.LENGTH_SHORT).show()
            finish()
        }
        val className = intent.getStringExtra("ClassName") ?: "Unknown Class"
        val classSP = intent.getStringExtra("Specialization") ?: "N/A"
        val classLevel = intent.getStringExtra("Level") ?: "N/A"
        val groupName = intent.getStringExtra("GroupName") ?: "N/A"
        val groupType = intent.getStringExtra("GroupType") ?: "N/A"
        val day = intent.getStringExtra("Day") ?: "N/A"
        val time = intent.getStringExtra("Time") ?: "N/A"

        // Set data to TextViews
        findViewById<TextView>(R.id.classNameSe).text = className
        findViewById<TextView>(R.id.seClassSP).text = classSP
        findViewById<TextView>(R.id.seClassLevel).text = classLevel
        findViewById<TextView>(R.id.seGroupName).text = groupName
        findViewById<TextView>(R.id.seGroupType).text = groupType
        findViewById<TextView>(R.id.tvDayP).text = day
        findViewById<TextView>(R.id.timeTvP).text = time

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSessionSe)
        univPresenceRecyclerAdapter = UnivPresenceRecyclerAdapter(dataW(groupId, className, classSP, classLevel, groupName, groupType, day, time))
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = univPresenceRecyclerAdapter
        }

        // Handle Add Presence button click
        findViewById<Button>(R.id.Session).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val database = LeafLetLocalDatabase.getDatabase(this@sessionPlanerVeiwActivity)
                val sessionPlanDao = database.sessionDao()

                // Get the current date
                val currentDate = LocalDate.now().toString() // Format: "YYYY-MM-DD"

                // Create a new session
                val newSession = UnivSession(
                    date = currentDate, // Use the current date
                    univPlanerId = planerId // Use the existing `planerId` variable
                )
                val sessionId = sessionPlanDao.insertSession(newSession)

                withContext(Dispatchers.Main) {
                    // Pass the session ID to the new activity
                    val intent = Intent(this@sessionPlanerVeiwActivity, viewPresenceActivity::class.java)
                    intent.apply {
                        putExtra("SessionID", sessionId.toInt())
                        putExtra("GroupID", groupId)
                        putExtra("PlanerID", planerId)
                        putExtra("ClassName", className)
                        putExtra("ClassSP", classSP)
                        putExtra("ClassLevel", classLevel)
                        putExtra("GroupName", groupName)
                        putExtra("GroupType", groupType)
                    }
                    startActivity(intent)
                }
            }
        }

        // Load data from the database
        val database = LeafLetLocalDatabase.getDatabase(this)
        val sessionPlanDao = database.sessionDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedSessionPlans = sessionPlanDao.getSessionByPlanerId(planerId)
            withContext(Dispatchers.Main) {
                univPresenceRecyclerAdapter.updateData(fetchedSessionPlans)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val database = LeafLetLocalDatabase.getDatabase(this)
        val sessionPlanDao = database.sessionDao()

        val planerId = intent.getIntExtra("PlanerID", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedSessionPlans = sessionPlanDao.getSessionByPlanerId(planerId)
            withContext(Dispatchers.Main) {
                univPresenceRecyclerAdapter.updateData(fetchedSessionPlans)
            }
        }
    }

    fun go_back(view: View) {
        finish()
    }
}