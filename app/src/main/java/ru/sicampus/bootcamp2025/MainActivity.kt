package ru.sicampus.bootcamp2025

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sicampus.bootcamp2025.data.auth.storage.AuthTokenManagerST
import ru.sicampus.bootcamp2025.util.navigateTo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        this.init()
        val navController = this.setupNavigation()

        if (isUserAuthenticated()) navigateTo(navController, R.id.action_nav_main_to_nav_map) else navigateTo(navController, R.id.action_nav_main_to_nav_auth)
    }

    private fun init() {
        AuthTokenManagerST.createInstance(this)
    }

    private fun setupNavigation(): NavController {
        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar);
        setSupportActionBar(toolbar)

        // Set up the BottomNavigationView
        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation);

        // Find the NavHostFragment and retrieve the NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment == null) throw IllegalStateException("NavHostFragment is null")

        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_map, R.id.nav_profile, R.id.nav_auth))
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->  // TODO: Refactor
            Log.d("Navigate", "Navigate to " + destination.label)
            navView.visibility = if (destination.id == R.id.nav_auth) View.GONE else View.VISIBLE
        }

        return navController
    }

    private fun isUserAuthenticated(): Boolean {
        return AuthTokenManagerST.getInstance().hasToken()  // FIXME
    }
}