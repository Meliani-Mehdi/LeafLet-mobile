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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class viewPresenceActivity : AppCompatActivity() {
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

        val sessionId = intent.getIntExtra("SessionID", 0)
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
                    outRect.set(0, 0, 0, 0) // No spacing between items
                }
            })
        }


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
                stAdapter.updateList(fetchedP)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val database = LeafLetLocalDatabase.getDatabase(this)
        val presenceDao = database.presenceDao()

        val sessionId = intent.getIntExtra("SessionID", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedP = presenceDao.getPresenceBySessionId(sessionId)
            withContext(Dispatchers.Main){
                stAdapter.updateList(fetchedP)
            }
        }
    }

    fun go_back(view: View) {
        finish()
    }
}