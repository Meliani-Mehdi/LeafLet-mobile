package com.app.leaflet

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class viewPresenceActivity : AppCompatActivity() {

    private lateinit var studentViewModel: StudentViewModel
    private lateinit var stP: stPViewModel
    private lateinit var stA: stAViewModel
    private lateinit var stABJ: stAbjViewModel



    private var sessionId: Int = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var stAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_presence)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val group_Id = intent.getIntExtra("GroupID", 0)
        if (group_Id == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404 ", Toast.LENGTH_SHORT).show()
            finish()
        }

        sessionId = intent.getIntExtra("SessionID", 0)
        if (sessionId == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404 ", Toast.LENGTH_SHORT).show()
            finish()
        }

        (findViewById<TextView>(R.id.className)).text = intent.getStringExtra("ClassName")
        (findViewById<TextView>(R.id.stClassSP)).text = intent.getStringExtra("ClassSP")
        (findViewById<TextView>(R.id.stClassLevel)).text = intent.getStringExtra("ClassLevel")
        (findViewById<TextView>(R.id.GroupName)).text = intent.getStringExtra("GroupName")
        (findViewById<TextView>(R.id.stGroupType)).text = intent.getStringExtra("GroupType")

        recyclerView = findViewById(R.id.recyclerViewStudent)
        stAdapter = StudentAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stAdapter
            clipToPadding = false
            clipChildren = false
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.set(0, 0, 0, 0)
                }
            })
        }

        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)
        stP = ViewModelProvider(this).get(stPViewModel::class.java)
        stA = ViewModelProvider(this).get(stAViewModel::class.java)
        stABJ = ViewModelProvider(this).get(stAbjViewModel::class.java)

        studentViewModel.studentCount.observe(this) { count ->
            (findViewById<TextView>(R.id.StudentNum)).text = "Number Of Students: $count"
        }
        stP.studentCount.observe(this) { count ->
            (findViewById<TextView>(R.id.pNum)).text = "Present: $count"
        }
        stA.studentCount.observe(this) { count ->
            (findViewById<TextView>(R.id.aNum)).text = "Absent: $count"
        }
        stABJ.studentCount.observe(this) { count ->
            (findViewById<TextView>(R.id.abjNum)).text = "ABJ: $count"
        }

        stAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                studentViewModel.updateStudentCount(stAdapter.itemCount)
                stP.updateStudentCount(stAdapter.getPrCount())
                stA.updateStudentCount(stAdapter.getAbCount())
                stABJ.updateStudentCount(stAdapter.getAbjCount())
            }
        })


        (findViewById<Button>(R.id.addStudentBtn)).setOnClickListener {
            intent = Intent(this, StudentActivity::class.java)
            intent.putExtra("GroupID", group_Id)
            startActivity(intent)
        }

        val database = LeafLetLocalDatabase.getDatabase(this)
        val presenceDao = database.presenceDao()

        lifecycleScope.launch(Dispatchers.IO) {
            presenceDao.insertMissingStudentsForSession(sessionId)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedP = presenceDao.getPresenceBySessionId(sessionId)
            withContext(Dispatchers.Main){
                stAdapter.updateData(fetchedP)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val database = LeafLetLocalDatabase.getDatabase(this)
        val presenceDao = database.presenceDao()

        lifecycleScope.launch(Dispatchers.IO) {
            presenceDao.insertMissingStudentsForSession(sessionId)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedP = presenceDao.getPresenceBySessionId(sessionId)
            withContext(Dispatchers.Main){
                stAdapter.updateData(fetchedP)
            }
        }
    }

    fun go_back(view: View) {
        finish()
    }
}