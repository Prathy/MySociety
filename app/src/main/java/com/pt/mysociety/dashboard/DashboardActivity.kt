package com.pt.mysociety.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pt.mysociety.BaseActivity
import com.pt.mysociety.R
import com.pt.mysociety.data.UserHelper
import com.pt.mysociety.databinding.ActivityDashboardBinding
import com.pt.mysociety.login.LoginActivity

interface FabClickListener {
    fun onFabClick()
}

class DashboardActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private var fabClickListener: FabClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_sports, R.id.nav_events
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView: View = navView.getHeaderView(0)
        val tvUsername: TextView = headerView.findViewById(R.id.username) as TextView
        tvUsername.text = sharedPreference.getDisplayName()

        binding.appBarMain.fab.setOnClickListener {
            fabClickListener?.onFabClick()
        }
        showFab(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                sharedPreference.setUserId(null)
//                sharedPreference.setUsername(null)
                sharedPreference.setRole(null)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setFabClickListener(callback: FabClickListener) {
        this.fabClickListener = callback
    }

    fun showFab(show: Boolean) {
        if(UserHelper.isAdmin(this) && show){
            binding.appBarMain.fab.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fab.visibility = View.INVISIBLE
        }
    }
}