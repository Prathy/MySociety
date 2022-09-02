package com.pt.mysociety.dashboard

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.pt.mysociety.R

interface FilterListener {
    fun onQueryTextChange(query: String)
}

open class FilterFragment : BaseFragment(), MenuProvider {

    private lateinit var filterListener: FilterListener

    protected fun setFilterListener(filterListener: FilterListener) {
        this.filterListener = filterListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val item = menu.findItem(R.id.action_search)
        item.isVisible = true
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterListener.onQueryTextChange(query ?: "")
                return true
            }
        })
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

}