package com.pt.mysociety.dashboard.sports.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.dashboard.sports.*
import com.pt.mysociety.data.DateHelper
import com.pt.mysociety.data.RandomHelper
import com.pt.mysociety.databinding.FragmentEquipmentDetailsBinding

class SportEquipmentDetailsFragment : Fragment() {

    private var _binding: FragmentEquipmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        _binding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        (activity as DashboardActivity).showFab(false)

        val sportId = arguments?.get("sportId") as String

        val root: View = binding.root
        val sdCategory: AutoCompleteTextView = binding.category
        val etDesc: EditText = binding.desc
        val etQuantity: EditText = binding.quantity
        val sdStatus: AutoCompleteTextView = binding.status
        val etUpdatedOn: EditText = binding.updatedOn
        val btUpdateEquipment = binding.updateEquipment

        etUpdatedOn.setText(DateHelper.toSimpleString())

        fun setCategoryAdapter(equipments: List<String>) {
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                equipments
            )
            sdCategory.setAdapter(categoryAdapter)
        }

        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            EquipmentStatus.values()
        )
        sdStatus.setAdapter(statusAdapter)

        btUpdateEquipment.setOnClickListener {
            val equipment = Equipment()
            equipment.id = RandomHelper.randomUUID()
            equipment.category = sdCategory.text.toString()
            equipment.description = etDesc.text.toString()
            equipment.quantity = etQuantity.text.toString().toInt()
            equipment.status = sdStatus.text.toString()
            equipment.updatedOn = etUpdatedOn.text.toString()
            sport.equipments[equipment.id] = equipment

            sportsViewModel.save(sport)
            findNavController().popBackStack()
        }

        sportsViewModel.getSport(sportId)
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            sport = it
            setCategoryAdapter(sport.equipmentCategories)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}