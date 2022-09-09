package com.pt.mysociety.dashboard.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.dashboard.FabClickListener
import com.pt.mysociety.dashboard.events.EventsViewModel
import com.pt.mysociety.dashboard.events.EventsViewModelFactory
import com.pt.mysociety.dashboard.sports.SportsViewModel
import com.pt.mysociety.dashboard.sports.SportsViewModelFactory
import com.pt.mysociety.databinding.FragmentPageBinding

class ExpensesFragment : Fragment(), AdapterItemEventListener, FabClickListener {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sportsViewModel: SportsViewModel
    private lateinit var eventsViewModel: EventsViewModel
    private val adapter = SportExpensesAdapter()
    private lateinit var sportId: String
    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as DashboardActivity).setFabClickListener(this)

        sportId = (arguments?.get("sportId") ?: "") as String
        eventId = (arguments?.get("eventId") ?: "") as String

        val rvSports : RecyclerView = binding.rvItems
        rvSports.itemAnimator = null
        rvSports.adapter = adapter
        adapter.setListener(this)
        val loading = binding.loading
        sportsViewModel.isLoading.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.VISIBLE else View.GONE
        }
        eventsViewModel.isLoading.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.VISIBLE else View.GONE
        }

        val tvMessage = binding.tvMessage
        sportsViewModel.isDataExist.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.GONE else loading.visibility
            tvMessage.visibility = if(it) View.GONE else View.VISIBLE
        }
        eventsViewModel.isDataExist.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.GONE else loading.visibility
            tvMessage.visibility = if(it) View.GONE else View.VISIBLE
        }

        fun setExpenses(expenses: List<Expense>) {
            adapter.setSportExpenses(expenses)
            tvMessage.visibility = if(expenses.isNotEmpty()) View.GONE else View.VISIBLE
        }

        if(sportId.isNotEmpty()) {
            sportsViewModel.getSport(sportId)
        }
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            setExpenses(it.expenses.values.toList())
        }

        if(eventId.isNotEmpty()) {
            eventsViewModel.getEvent(eventId)
        }
        eventsViewModel.event.observe(viewLifecycleOwner) {
            setExpenses(it.expenses.values.toList())
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).showFab(true)
    }

    override fun onPause() {
        super.onPause()
        sportsViewModel.onPause()
    }

    override fun onItemClick(item: Any){
        findNavController().navigate(
            R.id.action_nav_expenses_to_expense_details,
            bundleOf(
                Pair("sportId", sportId),
                Pair("eventId", eventId),
                Pair("expenseId", (item as Expense).id)
            )
        )
    }

    override fun onFabClick() {
        findNavController().navigate(
            R.id.action_nav_expenses_to_expense_details, bundleOf(
                Pair("sportId", sportId),
                Pair("eventId", eventId)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}