package com.pt.mysociety.dashboard.member

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
import com.pt.mysociety.dashboard.FilterListener
import com.pt.mysociety.databinding.FragmentPageBinding
import com.pt.mysociety.login.model.User

class MembersFragment : Fragment(), AdapterItemEventListener, FabClickListener, FilterListener {

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var membersViewModel: MembersViewModel
    private val adapter = MembersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        membersViewModel = ViewModelProvider(this, MembersViewModelFactory())[MembersViewModel::class.java]
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as DashboardActivity).setFabClickListener(this)
        (activity as DashboardActivity).setFilterListener(this)

        val rvSports : RecyclerView = binding.rvItems
        rvSports.itemAnimator = null
        rvSports.adapter = adapter
        adapter.setListener(this)
        membersViewModel.members.observe(viewLifecycleOwner) {
            adapter.setMembers(it)
        }

        val loading = binding.loading
        membersViewModel.isLoading.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.VISIBLE else View.GONE
        }

        val tvMessage = binding.tvMessage
        membersViewModel.isDataExist.observe(viewLifecycleOwner) {
            loading.visibility = if(it) View.GONE else loading.visibility
            tvMessage.visibility = if(it) View.GONE else View.VISIBLE
        }

        membersViewModel.init()
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).showFab(true)
    }

    override fun onPause() {
        super.onPause()
        membersViewModel.onPause()
    }

    override fun onItemClick(item: Any){
        findNavController().navigate(
            R.id.action_nav_members_to_member_details, bundleOf(
                Pair("memberId", (item as User).id)
            )
        )
    }

    override fun onFabClick() {
        findNavController().navigate(R.id.action_nav_members_to_member_details)
    }

    override fun onQueryTextChange(query: String) {
        adapter.filter.filter(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}