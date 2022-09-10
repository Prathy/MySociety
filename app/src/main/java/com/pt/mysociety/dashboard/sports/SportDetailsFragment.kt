package com.pt.mysociety.dashboard.sports

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
import com.pt.mysociety.databinding.FragmentSportDetailsBinding

class SportDetailsFragment : Fragment() {

    private var _binding: FragmentSportDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        _binding = FragmentSportDetailsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val etName: EditText = binding.name
        val etTag: EditText = binding.tag
        val btnSave = binding.save
        val llActions = binding.actions
        val btnExpenses = binding.expenses
        val btnFunds = binding.funds
        val btnEquipments = binding.equipments
        val tvTotalFunds = binding.totalFunds
        val tvTotalExpenses = binding.totalExpenses
        val tvBalance = binding.balance

        val sportId = (arguments?.get("sportId") ?: "") as String

        if(!UserHelper.isAdmin(requireContext())) {
            etName.isEnabled = false
            etTag.isEnabled = false
            btnSave.visibility = View.INVISIBLE
        }
        llActions.visibility = if(sportId.isEmpty()) View.INVISIBLE else View.VISIBLE

        btnSave.setOnClickListener {
            sport = sportsViewModel.sport.value ?: Sport()
            sport.name = etName.text.toString()
            sport.ownerId = SharedPreference(this.requireContext()).getUserId()
            sport.createdOn = DateHelper.toSimpleString()
            sport.tag = etTag.text.toString()

            sportsViewModel.save(sport)
            findNavController().popBackStack()
        }

        btnExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_nav_sport_details_to_expenses, bundleOf(
                Pair("sportId", sportId)
            )
            )
        }

        btnFunds.setOnClickListener {
            findNavController().navigate(R.id.action_nav_sport_details_to_funds, bundleOf(
                Pair("sportId", sportId)
            )
            )
        }

        btnEquipments.setOnClickListener {
            findNavController().navigate(R.id.action_nav_sport_details_to_equipments, bundleOf(
                Pair("sportId", sportId)
            )
            )
        }

        sportsViewModel.getSport(sportId)
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            if(it !== null) {
                etName.setText(it.name)
                etTag.setText(it.tag)

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