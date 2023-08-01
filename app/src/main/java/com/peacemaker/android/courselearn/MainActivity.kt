package com.peacemaker.android.courselearn

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.peacemaker.android.courselearn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val appBarConfiguration = setOf(
        R.id.splashScreenFragment,
        R.id.navigation_home,
        R.id.navigation_dashboard,
        R.id.navigation_notifications
    )//disable nav back arrows

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
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
        appBarConfig: Set<Int>,
    ) {
        val drawerLayout: DrawerLayout? = drawerLayoutId?.let { findViewById(it) }
        val appBarConfiguration = AppBarConfiguration(appBarConfig, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun showVisibilityForBottomNav(visibility: Boolean){
        if (visibility){
            binding.navView.visibility = View.VISIBLE
        }else{
            binding.navView.visibility = View.GONE
        }
    }
    private fun addOnDestinationChangedListener(navController: NavController){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showBottomNavOnIDs = listOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
            if (destination.id in showBottomNavOnIDs) {
                showVisibilityForBottomNav(true)
                if (destination.id == R.id.navigation_home){
                    Log.d("Main","Home Fragment")
                   // binding.customToolbar.visibility = View.VISIBLE
                }else {
                    //binding.customToolbar.visibility = View.GONE
                }
            } else {
                showVisibilityForBottomNav(false)
              //  binding.customToolbar.visibility = View.GONE
            }
        }
    }
}