package com.app.leaflet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class Main_window : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_window)

        val bottomNavBar = findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)

        if (savedInstanceState == null) {
            bottomNavBar.setItemSelected(R.id.nav_home, true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        bottomNavBar.setOnItemSelectedListener { id ->
            val selectedFragment: Fragment = when (id) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_time -> TimeFragment()
                R.id.nav_class -> classViewerFragment()
                R.id.nav_student -> StudentFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment() // Default case
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}