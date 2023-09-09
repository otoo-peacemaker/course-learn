package com.peacemaker.android.courselearn

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.peacemaker.android.courselearn.databinding.ActivityMainBinding
import com.peacemaker.android.courselearn.ui.StatusFragment
import com.peacemaker.android.courselearn.ui.courses.ClassroomFragment
import com.peacemaker.android.courselearn.ui.message.MessageDetailsFragment
import com.peacemaker.android.courselearn.ui.util.NetworkConnectivity
import com.peacemaker.android.courselearn.ui.util.Utils
import com.peacemaker.android.courselearn.ui.util.Utils.startFragmentFromAnywhere


class MainActivity : AppCompatActivity() {
    private val viewModel: BaseViewModel by viewModels()
    private lateinit var networkConnectivity: NetworkConnectivity
     lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val appBarConfiguration = setOf(
        R.id.splashScreenFragment, R.id.boardingScreenFragment,
        R.id.navigation_home, R.id.navigation_course,
        R.id.navigation_message, R.id.successFragment,
        R.id.navigation_account
    )//hide nav back arrows

    override fun onStart() {
        super.onStart()
        networkConnectivity.startListening()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        setSupportActionBar(binding.toolbar)
        val bar: ActionBar? = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavMenu(navController)
        setupActionBarWithNavController(
            navController = navController,
            appBarConfig = appBarConfiguration
        )
        addOnDestinationChangedListener(navController)
        setOnclickListeners()
        //notificationFragment()
        observeBadgeCount()
        //setScreenOrientation()
        networkConnectivity = NetworkConnectivity(this)
        if (!networkConnectivity.isConnected()) {
//            val navCont = NavHostFragment.findNavController(StatusFragment())
          //  startFragmentFromAnywhere(R.id.statusFragment, navController)
        }
        }

    private fun setOnclickListeners(){
        binding.apply {
            fab.setOnClickListener {
                navController.navigate(R.id.action_global_searchFragment)
            }
            myCourses.setOnClickListener {
                navController.navigate(R.id.action_global_myCoursesFragment)
            }
        }
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav: BottomNavigationView = binding.navView
        bottomNav.background = null
        bottomNav.setupWithNavController(navController)
    }

    private fun setupActionBarWithNavController(
        navController: NavController,
        drawerLayoutId: Int? = null,
        appBarConfig: Set<Int>) {
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
            val showBottomNavOnIDs = listOf(
                R.id.navigation_home, R.id.navigation_course,
                R.id.navigation_message, R.id.navigation_account)
            if (destination.id in showBottomNavOnIDs) {
                showVisibilityForBottomNav(true)
                if (destination.id == R.id.navigation_home){
                    binding.customToolbar.visibility = View.VISIBLE
                    binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
                    // Change the status bar color dynamically
                    val statusBarColor = ContextCompat.getColor(this, R.color.primary)
                    changeStatusBarColor(window, statusBarColor)

                }else {
                    binding.customToolbar.visibility = View.GONE
                    binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.titleBackground))
                    val statusBarColor = ContextCompat.getColor(this, R.color.titleBackground)
                    changeStatusBarColor(window, statusBarColor)
                }
            } else {
                showVisibilityForBottomNav(false)
                  binding.customToolbar.visibility = View.GONE
            }
        }
    }
    //Enable navigate up
    override fun onSupportNavigateUp(): Boolean {
        val appBarConfiguration = AppBarConfiguration(appBarConfiguration)
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp(appBarConfiguration)
    }

    private fun changeStatusBarColor(window: Window, color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    /**
     * A function to start a fragment using pending intent
     * */

    private fun notificationFragment(){
        // Check if there's an intent with specific data
        val fragmentToOpen = intent.getStringExtra("fragmentToOpen")
        if (fragmentToOpen == "MessageDetailsFragment") {
            val fragment = MessageDetailsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .commit()
        }
    }

    fun updateBadgeCount(count: Int) {
        viewModel.updateUnreadNotificationsCount(count)
    }
    private fun observeBadgeCount() {
        viewModel.unreadNotificationsCount.observe(this) { unreadCount ->
            val bottomNavigationView = binding.navView
            val menuItemId = R.id.navigation_message // Update with the correct menu item ID
            val menuItem = bottomNavigationView.menu.findItem(menuItemId)

            // Create or update the badge
            val badgeDrawable = BadgeDrawable.create(this)
            if (unreadCount > 0) {
                badgeDrawable.number = unreadCount
                menuItem.icon = badgeDrawable
            } else {
                menuItem.icon = null // Remove the badge
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setScreenOrientation(){

        // Check the active fragment and adjust orientation if needed
        val activeFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        requestedOrientation = if (activeFragment is ClassroomFragment) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }else{
            // Lock the activity orientation to portrait mode
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkConnectivity.stopListening()
    }
}