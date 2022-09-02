package com.pt.mysociety.dashboard.fund

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.BaseFragment
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.dashboard.events.Event
import com.pt.mysociety.dashboard.events.EventsViewModel
import com.pt.mysociety.dashboard.events.EventsViewModelFactory
import com.pt.mysociety.dashboard.sports.*
import com.pt.mysociety.data.DateHelper
import com.pt.mysociety.data.RandomHelper
import com.pt.mysociety.data.UserHelper
import com.pt.mysociety.databinding.FragmentFundDetailsBinding
import com.pt.mysociety.login.model.User

class FundDetailsFragment : BaseFragment() {

    private var _binding: FragmentFundDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport
    private lateinit var event: Event
    private lateinit var fund: Fund

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        val eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentFundDetailsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val etFrom: AutoCompleteTextView = binding.from
        val etAmount: EditText = binding.amount
        val etContributedOn: EditText = binding.contributedOn
        val btAddFund = binding.addFund

        val sportId = (arguments?.get("sportId") ?: "") as String
        val eventId = (arguments?.get("eventId") ?: "") as String
        val fundId: String = (arguments?.get("fundId") ?: "") as String

        btAddFund.visibility = if(fundId.isNotEmpty()) View.INVISIBLE else View.VISIBLE
        etContributedOn.setText(DateHelper.toSimpleString())

        var memberNames: Array<String> = arrayOf()
        var fromId = sharedPreference.getUserId()

        membersViewModel.members.observe(viewLifecycleOwner) {
            memberNames = arrayOf()
            it.forEach { member ->
                memberNames = memberNames.plus(getString(R.string.label_member_house, member.name, UserHelper.getHouse(member)))
            }
            val membersAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                memberNames
            )
            etFrom.setAdapter(membersAdapter)
        }

        etFrom.setOnItemClickListener { _, _, _, _ ->
            val adapterPos = memberNames.indexOf(etFrom.text.toString())
            fromId = members[adapterPos].id
        }

        btAddFund.setOnClickListener {
            val fund = Fund()
            fund.id = RandomHelper.randomUUID()
            fund.from = fromId
            fund.amount = etAmount.text.toString().toInt()
            fund.contributedOn = etContributedOn.text.toString()

            if(sportId.isNotEmpty()) {
                sport.funds = sport.funds.plus(fund)
                sportsViewModel.save(sport)
            } else {
                event.funds = event.funds.plus(fund)
                eventsViewModel.save(event)
            }

            findNavController().popBackStack()
        }

        fun setFundDetails() {
            etFrom.isEnabled = false
            etAmount.isEnabled = false
            etContributedOn.isEnabled = false
            btAddFund.visibility = View.INVISIBLE

            val member = members.find { member ->
                member.id == fund.from
            } ?: User()

            fund.from = member.name + "(" + UserHelper.getHouse(member) + ")"

            etFrom.setText(fund.from)
            etAmount.setText(fund.amount.toString())
            etContributedOn.setText(fund.contributedOn)
        }

        if(fundId.isNotEmpty()) {
            sportsViewModel.getSport(sportId)
            sportsViewModel.getSportFund(sportId, fundId)
        }

        sportsViewModel.sport.observe(viewLifecycleOwner) {
            sport = it
            etAmount.setText(getString(R.string.minimum_fund_amount))
        }
        sportsViewModel.fund.observe(viewLifecycleOwner) {
            fund = it
            setFundDetails()
        }

        if(eventId.isNotEmpty()) {
            eventsViewModel.getEvent(eventId)
            eventsViewModel.getEventFund(eventId, fundId)
        }
        eventsViewModel.event.observe(viewLifecycleOwner) {
            event = it
            etAmount.setText(event.minContribution.toString())
        }
        eventsViewModel.fund.observe(viewLifecycleOwner) {
            fund = it
            setFundDetails()
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).showFab(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}