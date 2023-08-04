package com.peacemaker.android.courselearn

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.peacemaker.android.courselearn.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val appBarConfiguration = setOf(
        R.id.splashScreenFragment,
        R.id.boardingScreenFragment,
        R.id.navigation_home,
        R.id.navigation_course,
        R.id.navigation_message,
        R.id.successFragment
    )//hide nav back arrows

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        setSupportActionBar(binding.toolbar)
        val bar: ActionBar? = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
        //bar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.titleBackground)))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavMenu(navController)
        setupActionBarWithNavController(
            navController = navController,
            appBarConfig = appBarConfiguration
        )
        addOnDestinationChangedListener(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav: BottomNavigationView = binding.navView
        bottomNav.background = null
        bottomNav.setupWithNavController(navController)
    }

    private fun setupActionBarWithNavController(
        navController: NavController,
        drawerLayoutId: Int? = null,
        appBarConfig: Set<Int>
    ) {
        val drawerLayout: DrawerLayout? = drawerLayoutId?.let { findViewById(it) }
        val appBarConfiguration = AppBarConfiguration(appBarConfig, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun showVisibilityForBottomNav(visibility: Boolean) {
        if (visibility) {
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.fab.visibility = View.VISIBLE
        } else {
            binding.bottomAppBar.visibility = View.INVISIBLE
            binding.fab.visibility = View.INVISIBLE
        }
    }

    private fun addOnDestinationChangedListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideActionBar = listOf(
                R.id.boardingScreenFragment,
                R.id.loginFragment,
                R.id.signUpFragment,
            )
            if (destination.id in hideActionBar) {
//                hideActionBar()
                Log.d("hideActionBar","hide")
            } else {
               // showActionBar() TODO

            }

            val showBottomNavOnIDs = listOf(
                R.id.navigation_home, R.id.navigation_course,
                R.id.navigation_message, R.id.navigation_account)
            if (destination.id in showBottomNavOnIDs) {
                showVisibilityForBottomNav(true)
            } else {
                showVisibilityForBottomNav(false)
                //  binding.customToolbar.visibility = View.GONE
            }
        }
    }

    private fun showActionBar() {
        supportActionBar?.show()
    }

    private fun hideActionBar() {
        supportActionBar?.hide()
    }

    //Enable navigate up
    override fun onSupportNavigateUp(): Boolean {
        val appBarConfiguration = AppBarConfiguration(appBarConfiguration)
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp(appBarConfiguration)
    }
}