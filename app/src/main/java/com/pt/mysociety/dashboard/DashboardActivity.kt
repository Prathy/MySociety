package com.pt.mysociety.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
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

interface FilterListener {
    fun onQueryTextChange(query: String)
}

class DashboardActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private var fabClickListener: FabClickListener? = null
    private var filterListener: FilterListener? = null

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
                R.id.nav_sports, R.id.nav_events, R.id.nav_members
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
        menuInflater.inflate(R.menu.menu_profile, menu)
        val item = menu.findItem(R.id.action_search);
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterListener?.onQueryTextChange(query ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                sharedPreference.setUserId(null)
                sharedPreference.setLastLoginTime(0)
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

    fun setFilterListener(callback: FilterListener) {
        this.filterListener = callback
    }

    fun showFab(show: Boolean) {
        if(UserHelper.isAdmin(this) && show){
            binding.appBarMain.fab.visibility = View.VISIBLE
        } else {
            binding.appBarMain.fab.visibility = View.INVISIBLE
        }
    }
}