package com.pt.mysociety.dashboard.sports.equipment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val tvLabelEquipments = binding.labelEquipments
        val llActions: LinearLayout = binding.actions
        val rvEquipments : RecyclerView = binding.rvItems
        val loading = binding.loading
        val tvMessage = binding.tvMessage

        sportId = arguments?.get("sportId") as String

        rvEquipments.itemAnimator = null
        rvEquipments.adapter = adapter
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            loading.visibility = View.INVISIBLE
            if(it.equipments.values.toList().isEmpty()){
                tvMessage.visibility = View.VISIBLE
                llActions.visibility = View.GONE
                tvLabelEquipments.visibility = View.INVISIBLE
            } else {
                tvMessage.visibility = View.INVISIBLE
                llActions.visibility = View.VISIBLE
                tvLabelEquipments.visibility = View.VISIBLE
            }

            adapter.setSportEquipments(it.equipments.values.toList())
        }

        sportsViewModel.sport.observe(viewLifecycleOwner) {
            if(it !== null) {
                val equipmentMap: HashMap<String, Int> = HashMap()
                it.equipmentCategories.forEach { category ->
                    equipmentMap["A-$category"] = 0
                    equipmentMap["T-$category"] = 0
                }
                it.equipments.forEach { equipment ->
                    when(equipment.value.status) {
                        EquipmentStatus.Available.name -> {
                            equipmentMap["T-${equipment.value.category}"] =
                                (equipmentMap["T-${equipment.value.category}"] ?: 0) + equipment.value.quantity
                            equipmentMap["A-${equipment.value.category}"] =
                                (equipmentMap["A-${equipment.value.category}"] ?: 0) + equipment.value.quantity
                        }
                        EquipmentStatus.UnAvailable.name -> {
                            equipmentMap["A-${equipment.value.category}"] =
                                (equipmentMap["A-${equipment.value.category}"] ?: 0) - equipment.value.quantity
                        }
                    }
                }

                llActions.removeAllViews()
                it.equipmentCategories.forEach { key ->
                    val tvDynamic = TextView(requireContext())
                    tvDynamic.textSize = 18f
                    tvDynamic.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    tvDynamic.typeface = Typeface.DEFAULT_BOLD
                    tvDynamic.text = requireContext().getString(R.string.info_equipment_details, key, equipmentMap["A-$key"], equipmentMap["T-$key"])
                    llActions.addView(tvDynamic)
                }
            }
        }

        loading.visibility = View.VISIBLE
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