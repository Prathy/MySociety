package com.pt.mysociety.dashboard.sports.expense

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
import com.pt.mysociety.data.RandomHelper
import com.pt.mysociety.databinding.FragmentSportExpenseDetailsBinding

class SportExpenseDetailsFragment : Fragment() {

    private var _binding: FragmentSportExpenseDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport
    private lateinit var expense: Expense

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        _binding = FragmentSportExpenseDetailsBinding.inflate(inflater, container, false)
        (activity as DashboardActivity).showFab(false)

        val root: View = binding.root
        val sdCategory: AutoCompleteTextView = binding.category
        val etDesc: EditText = binding.desc
        val etPrice: EditText = binding.price
        val etQuantity: EditText = binding.quantity
        val etBuyer: EditText = binding.buyer
        val etAddedOn: EditText = binding.addedOn
        val btAddExpense = binding.addExpense

        val sportId = arguments?.get("sportId") as String
        val inventoryId: String = (arguments?.get("inventoryId") ?: "") as String

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            ExpenseCategory.values()
        )
        sdCategory.setAdapter(categoryAdapter)

        btAddExpense.setOnClickListener {
            val expense = Expense()
            expense.id = RandomHelper.randomUUID()
            expense.category = sdCategory.text.toString()
            expense.description = etDesc.text.toString()
            expense.price = etPrice.text.toString().toInt()
            expense.quantity = etQuantity.text.toString().toInt()
            expense.buyer = etBuyer.text.toString()
            expense.addedOn = etAddedOn.text.toString()
            sport.expenses = sport.expenses.plus(expense)

            if(EquipmentCategory.Bat.name == expense.category || EquipmentCategory.Ball.name == expense.category){
                val equipment = Equipment()
                equipment.id = RandomHelper.randomUUID()
                equipment.category = expense.category
                equipment.description = etDesc.text.toString()
                equipment.status = EquipmentStatus.Available.name
                equipment.quantity = expense.quantity
                equipment.updatedOn = etAddedOn.text.toString()
                sport.equipments = sport.equipments.plus(equipment)
            }

            sportsViewModel.save(sport)
            findNavController().popBackStack()
        }

        sportsViewModel.getSport(sportId)
        sportsViewModel.getSportInventory(sportId, inventoryId)
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            sport = it
        }
        sportsViewModel.expense.observe(viewLifecycleOwner) {
            expense = it
            sdCategory.isEnabled = false
            etDesc.isEnabled = false
            etPrice.isEnabled = false
            etQuantity.isEnabled = false
            etBuyer.isEnabled = false
            etAddedOn.isEnabled = false
            btAddExpense.visibility = View.INVISIBLE

            sdCategory.setText(expense.category, false)
            etDesc.setText(expense.description)
            etPrice.setText(expense.price.toString())
            etQuantity.setText(expense.quantity.toString())
            etBuyer.setText(expense.buyer)
            etAddedOn.setText(expense.addedOn)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}