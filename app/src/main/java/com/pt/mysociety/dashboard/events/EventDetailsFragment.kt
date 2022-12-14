package com.pt.mysociety.dashboard.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.R
import com.pt.mysociety.SharedPreference
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.helpers.CurrencyHelper
import com.pt.mysociety.helpers.DateHelper
import com.pt.mysociety.helpers.UserHelper
import com.pt.mysociety.databinding.FragmentEventDetailsBinding

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val etName: EditText = binding.name
        val etTag: EditText = binding.tag
        val etMinContribution: EditText = binding.minContribution
        val etDate: EditText = binding.eventDate
        val llActions = binding.actions
        val tvTotalFunds = binding.totalFunds
        val tvTotalExpenses = binding.totalExpenses
        val tvBalance = binding.balance
        val btnExpenses = binding.expenses
        val btnFunds = binding.funds
        val btCreateEvent = binding.createEvent

        val eventId: String = (arguments?.get("eventId") ?: "") as String
        if(!UserHelper.isAdmin(requireContext())) {
            etName.isEnabled = false
            etTag.isEnabled = false
            etDate.isEnabled = false
            etMinContribution.isEnabled = false
            btCreateEvent.visibility = View.INVISIBLE
        }
        llActions.visibility = if(eventId.isEmpty()) View.INVISIBLE else View.VISIBLE

        btCreateEvent.setOnClickListener {
            event = eventsViewModel.event.value ?: Event()
            event.name = etName.text.toString()
            event.tag = etTag.text.toString()
            event.minContribution = etMinContribution.text.toString().toInt()
            event.eventDate = etDate.text.toString()
            event.ownerId = SharedPreference(this.requireContext()).getUserId()
            event.createdOn = DateHelper.toSimpleString()

            eventsViewModel.save(event)
            findNavController().popBackStack()
        }

        btnExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_nav_event_details_to_expenses, bundleOf(
                Pair("eventId", eventId)
            )
            )
        }

        btnFunds.setOnClickListener {
            findNavController().navigate(R.id.action_nav_event_details_to_funds, bundleOf(
                Pair("eventId", eventId)
            )
            )
        }

        eventsViewModel.getEvent(eventId)
        eventsViewModel.event.observe(viewLifecycleOwner) {
            if(it !== null) {
                etName.setText(it.name)
                etTag.setText(it.tag)
                etMinContribution.setText(it.minContribution.toString())
                etDate.setText(it.eventDate)

                var totalFunds = 0
                it.funds.forEach { fund ->
                    totalFunds += fund.value.amount
                }
                tvTotalFunds.text =
                    getString(R.string.info_total_funds, CurrencyHelper.convertToRupees(totalFunds))

                var totalExpenses = 0
                it.expenses.forEach { item ->
                    totalExpenses += (item.value.price * item.value.quantity)
                }
                tvTotalExpenses.text = getString(
                    R.string.info_total_expenses,
                    CurrencyHelper.convertToRupees(totalExpenses)
                )

                tvBalance.text = getString(
                    R.string.info_balance,
                    CurrencyHelper.convertToRupees(totalFunds - totalExpenses)
                )
            }
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