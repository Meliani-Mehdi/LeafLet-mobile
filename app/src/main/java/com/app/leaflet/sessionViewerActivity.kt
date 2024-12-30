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

private lateinit var univPlanerRecyclerAdapter: UnivPlanerRecyclerAdapter
private lateinit var recyclerViewPlaner: RecyclerView

class sessionViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_session_viewer)
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

        (findViewById<TextView>(R.id.classNameSe)).text = intent.getStringExtra("ClassName")
        (findViewById<TextView>(R.id.seClassSP)).text = intent.getStringExtra("ClassSP")
        (findViewById<TextView>(R.id.seClassLevel)).text = intent.getStringExtra("ClassLevel")
        (findViewById<TextView>(R.id.seGroupName)).text = intent.getStringExtra("GroupName")
        (findViewById<TextView>(R.id.seGroupType)).text = intent.getStringExtra("GroupType")

        recyclerViewPlaner = findViewById(R.id.recyclerViewSessionSe)
        univPlanerRecyclerAdapter = UnivPlanerRecyclerAdapter()
        recyclerViewPlaner.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = univPlanerRecyclerAdapter
        }

        (findViewById<Button>(R.id.Session)).setOnClickListener {
            intent = Intent(this, sessionActivity::class.java)
            intent.putExtra("GroupID", group_Id)
            startActivity(intent)
        }

        val database = LeafLetLocalDatabase.getDatabase(this)
        val univPlanerDao = database.planerDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val fetechedPlaners = univPlanerDao.getPlanerByGroupId(group_Id)
            withContext(Dispatchers.Main){
                univPlanerRecyclerAdapter.updateList(fetechedPlaners)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val database = LeafLetLocalDatabase.getDatabase(this)
        val univPlanerDao = database.planerDao()

        val group_Id = intent.getIntExtra("GroupID", 0)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetechedPlaners = univPlanerDao.getPlanerByGroupId(group_Id)
            withContext(Dispatchers.Main){
                univPlanerRecyclerAdapter.updateList(fetechedPlaners)
            }
        }
    }

    fun go_back(view: View) {
        finish()
    }
}