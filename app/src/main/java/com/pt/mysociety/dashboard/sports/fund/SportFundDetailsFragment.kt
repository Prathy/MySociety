package com.pt.mysociety.dashboard.sports.fund

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.dashboard.sports.*
import com.pt.mysociety.data.RandomHelper
import com.pt.mysociety.databinding.FragmentSportFundDetailsBinding

class SportFundDetailsFragment : Fragment() {

    private var _binding: FragmentSportFundDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport
    private lateinit var fund: Fund

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        _binding = FragmentSportFundDetailsBinding.inflate(inflater, container, false)
        (activity as DashboardActivity).showFab(false)

        val root: View = binding.root
        val etFrom: EditText = binding.from
        val etAmount: EditText = binding.amount
        val etContributedOn: EditText = binding.contributedOn
        val btAddFund = binding.addFund

        val sportId = arguments?.get("sportId") as String
        val fundId: String = (arguments?.get("fundId") ?: "") as String

        btAddFund.setOnClickListener {
            val fund = Fund()
            fund.id = RandomHelper.randomUUID()
            fund.from = etFrom.text.toString()
            fund.amount = etAmount.text.toString().toInt()
            fund.contributedOn = etContributedOn.text.toString()
            sport.funds = sport.funds.plus(fund)

            sportsViewModel.save(sport)
            findNavController().popBackStack()
        }

        sportsViewModel.getSport(sportId)
        sportsViewModel.getSportFund(sportId, fundId)
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            sport = it
        }
        sportsViewModel.fund.observe(viewLifecycleOwner) {
            fund = it
            etFrom.isEnabled = false
            etAmount.isEnabled = false
            etContributedOn.isEnabled = false
            btAddFund.visibility = View.INVISIBLE

            etFrom.setText(fund.from)
            etAmount.setText(fund.amount.toString())
            etContributedOn.setText(fund.contributedOn)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}