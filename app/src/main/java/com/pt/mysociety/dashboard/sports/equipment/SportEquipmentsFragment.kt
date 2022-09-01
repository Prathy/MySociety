package com.pt.mysociety.dashboard.sports.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.dashboard.FabClickListener
import com.pt.mysociety.dashboard.sports.*
import com.pt.mysociety.databinding.FragmentEquipmentsBinding

class SportEquipmentsFragment : Fragment(), FabClickListener {

    private var _binding: FragmentEquipmentsBinding? = null
    private val binding get() = _binding!!
    private val adapter = SportEquipmentsAdapter()
    private lateinit var sportId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        _binding = FragmentEquipmentsBinding.inflate(inflater, container, false)
        (activity as DashboardActivity).setFabClickListener(this)

        val root: View = binding.root
        val tvBats: TextView = binding.bats
        val tvBalls: TextView = binding.balls
        val rvEquipments : RecyclerView = binding.rvItems

        sportId = arguments?.get("sportId") as String

        rvEquipments.itemAnimator = null
        rvEquipments.adapter = adapter
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            adapter.setSportEquipments(it.equipments)
        }

        sportsViewModel.sport.observe(viewLifecycleOwner) {
            if(it !== null) {
                var availableBats = 0
                var availableBalls = 0
                var totalBats = 0
                var totalBalls = 0
                it.equipments.forEach { equipment ->
                    when(equipment.status) {
                        EquipmentStatus.Available.name -> {
                            when (equipment.category) {
                                "Bat" -> {
                                    totalBats += equipment.quantity
                                    availableBats += equipment.quantity
                                }
                                "Ball" -> {
                                    totalBalls += equipment.quantity
                                    availableBalls += equipment.quantity
                                }
                            }
                        }
                        EquipmentStatus.UnAvailable.name -> {
                            when (equipment.category) {
                                "Bat" -> {
                                    availableBats -= equipment.quantity
                                }
                                "Ball" -> {
                                    availableBalls -= equipment.quantity
                                }
                            }
                        }
                    }
                }
                tvBats.text = getString(R.string.info_bats, availableBats, totalBats)
                tvBalls.text = getString(R.string.info_balls, availableBalls, totalBalls)
            }
        }

        sportsViewModel.getSport(sportId)

        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).showFab(true)
    }

    override fun onFabClick() {
        findNavController().navigate(
            R.id.action_nav_equipments_to_equipment_details, bundleOf(
                Pair("sportId", sportId)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}