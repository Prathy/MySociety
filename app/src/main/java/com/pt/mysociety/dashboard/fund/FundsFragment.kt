package com.pt.mysociety.dashboard.fund

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.*
import com.pt.mysociety.dashboard.events.EventsViewModel
import com.pt.mysociety.dashboard.events.EventsViewModelFactory
import com.pt.mysociety.dashboard.sports.*
import com.pt.mysociety.databinding.FragmentPageBinding
import com.pt.mysociety.login.model.User

class FundsFragment : BaseFragment(), AdapterItemEventListener, FabClickListener, FilterListener {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sportsViewModel: SportsViewModel
    private lateinit var eventsViewModel: EventsViewModel
    private val adapter = SportFundsAdapter()
    private lateinit var sportId: String
    private lateinit var eventId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as DashboardActivity).setFabClickListener(this)
        (activity as DashboardActivity).setFilterListener(this)

        sportId = (arguments?.get("sportId") ?: "") as String
        eventId = (arguments?.get("eventId") ?: "") as String

        val rvSports : RecyclerView = binding.rvItems
        rvSports.itemAnimator = null
        rvSports.adapter = adapter
        adapter.setListener(this)

        fun setFunds(funds: List<Fund>) {
            funds.forEach { fund ->
                val member: User = members.find { member ->
                    member.id == fund.from
                } ?: User()

                fund.from = member.name ?: ""
            }
            adapter.setSportFunds(funds)
        }

        sportsViewModel.sport.observe(viewLifecycleOwner) {
            setFunds(it.funds)
        }

        eventsViewModel.event.observe(viewLifecycleOwner) {
            setFunds(it.funds)
        }

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

        if(sportId.isNotEmpty()) {
            sportsViewModel.getSport(sportId)
        } else {
            eventsViewModel.getEvent(eventId)
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
            R.id.action_nav_funds_to_fund_details, bundleOf(
                Pair("sportId", sportId), Pair("eventId", eventId), Pair("fundId", (item as Fund).id)
            )
        )
    }

    override fun onFabClick() {
        findNavController().navigate(
            R.id.action_nav_funds_to_fund_details, bundleOf(
                Pair("sportId", sportId), Pair("eventId", eventId)
            )
        )
    }

    override fun onQueryTextChange(query: String) {
        adapter.filter.filter(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}