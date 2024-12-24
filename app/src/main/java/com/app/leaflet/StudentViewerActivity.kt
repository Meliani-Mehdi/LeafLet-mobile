package com.app.leaflet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var univStudentRecyclerAdapter: UnivStudentRecyclerAdapter
private lateinit var recyclerViewStudent: RecyclerView

class StudentViewerActivity : AppCompatActivity() {

    private lateinit var studentViewModel: StudentViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_viewer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val group_Id = intent.getIntExtra("GroupID", 0) //use this to link the excel function
        if (group_Id == 0) {
            Toast.makeText(this, "An error has occurred, error code: 2404 ", Toast.LENGTH_SHORT).show()
            finish()
        }

        (findViewById<Button>(R.id.addStudentBtn)).setOnClickListener {
            intent = Intent(this, StudentActivity::class.java)
            intent.putExtra("GroupID", group_Id)
            startActivity(intent)
        }

        recyclerViewStudent = findViewById(R.id.recyclerViewStudent)
        univStudentRecyclerAdapter = UnivStudentRecyclerAdapter()
        recyclerViewStudent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = univStudentRecyclerAdapter
        }

        studentViewModel = ViewModelProvider(this).get(StudentViewModel::class.java)

        studentViewModel.studentCount.observe(this) { count ->
            (findViewById<TextView>(R.id.StudentNum)).text = "Number Of Students: $count"
        }

        univStudentRecyclerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                studentViewModel.updateStudentCount(univStudentRecyclerAdapter.itemCount)
            }
        })

        val database = LeafLetLocalDatabase.getDatabase(this)
        val univStudentDao = database.studentDao()
        val univGroupDao = database.groupDao()
        val univClassDao = database.univClassDao()

        val tvClassName = findViewById<TextView>(R.id.className)
        val tvStClassSP = findViewById<TextView>(R.id.stClassSP)
        val tvStClassLevel = findViewById<TextView>(R.id.stClassLevel)
        val tvGroupName = findViewById<TextView>(R.id.GroupName)
        val tvStGroupType = findViewById<TextView>(R.id.stGroupType)

        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedStudents = univStudentDao.getStudentByGroupId(group_Id)
            withContext(Dispatchers.Main) {
                univStudentRecyclerAdapter.updateList(fetchedStudents)
            }
            val fetchedGroup = univGroupDao.getGroupById(group_Id)
            var fetchedClass = UnivClass(0, "Class Name", "error", "error", "error")
            if(fetchedGroup!=null){
                fetchedClass = fetchedGroup.univClassId?.let { univClassDao.getClassById(it) }!!
            }
            withContext(Dispatchers.Main) {
                tvClassName.text = fetchedClass.name
                tvStClassSP.text = fetchedClass.specialty
                tvStClassLevel.text = fetchedClass.level
                if (fetchedGroup != null) {
                    tvGroupName.text = fetchedGroup.name
                    tvStGroupType.text = fetchedGroup.type
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val group_Id = intent.getIntExtra("GroupID", 0)
        val database = LeafLetLocalDatabase.getDatabase(this)
        val univStudentDao = database.studentDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val fetchedStudents = univStudentDao.getStudentByGroupId(group_Id)
            withContext(Dispatchers.Main) {
                univStudentRecyclerAdapter.updateList(fetchedStudents)
            }
        }
    }

    fun go_back(view: View) {
        finish()
    }
}