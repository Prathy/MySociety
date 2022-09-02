package com.pt.mysociety.dashboard.events

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
import com.pt.mysociety.databinding.FragmentPageBinding

class EventsFragment : Fragment(), AdapterItemEventListener, FabClickListener {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private val adapter = EventsAdapter()
    private lateinit var eventsViewModel: EventsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as DashboardActivity).setFabClickListener(this)

        val rvEvents : RecyclerView = binding.rvItems
        rvEvents.itemAnimator = null
        rvEvents.adapter = adapter
        adapter.setListener(this)
        eventsViewModel.events.observe(viewLifecycleOwner) {
            adapter.setEvents(it)
        }

        val loading = binding.loading
        eventsViewModel.isLoading.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.VISIBLE else View.GONE
        }

        val tvMessage = binding.tvMessage
        eventsViewModel.isDataExist.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.GONE else loading.visibility
            tvMessage.visibility = if(it) View.GONE else View.VISIBLE
        }

        eventsViewModel.init()
        return root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        eventsViewModel.onPause()
    }

    override fun onItemClick(item: Any){
        findNavController().navigate(R.id.action_nav_events_to_event_details,
            bundleOf(
                Pair("eventId", (item as Event).id)
            )
        )
    }

    override fun onFabClick() {
        findNavController().navigate(R.id.action_nav_events_to_event_details)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}