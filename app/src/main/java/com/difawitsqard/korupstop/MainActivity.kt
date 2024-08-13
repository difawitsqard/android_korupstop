package com.difawitsqard.korupstop

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.difawitsqard.korupstop.view.EducationFragment
import com.difawitsqard.korupstop.view.home.HomeFragment
import com.difawitsqard.korupstop.view.SubmitReportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(
                getColor(R.color.primary),
                getColor(R.color.soft_dark)
            )
        )
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.nav_submit_report -> {
                    replaceFragment(SubmitReportFragment())
                    true
                }

                R.id.nav_education -> {
                    replaceFragment(EducationFragment())
                    true
                }

                else -> false
            }
        }
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }
}