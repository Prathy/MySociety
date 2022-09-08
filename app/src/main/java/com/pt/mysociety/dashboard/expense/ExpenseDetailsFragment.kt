package com.pt.mysociety.dashboard.expense

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
import com.pt.mysociety.databinding.FragmentExpenseDetailsBinding
import com.pt.mysociety.login.model.User

class ExpenseDetailsFragment : BaseFragment() {

    private var _binding: FragmentExpenseDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sport: Sport
    private lateinit var event: Event
    private lateinit var expense: Expense

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val sportsViewModel = ViewModelProvider(this, SportsViewModelFactory())[SportsViewModel::class.java]
        val eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]
        _binding = FragmentExpenseDetailsBinding.inflate(inflater, container, false)
        (activity as DashboardActivity).showFab(false)

        val root: View = binding.root
        val sdCategory: AutoCompleteTextView = binding.category
        val etDesc: EditText = binding.desc
        val etPrice: EditText = binding.price
        val etQuantity: EditText = binding.quantity
        val etFrom: AutoCompleteTextView = binding.buyer
        val etAddedOn: EditText = binding.addedOn
        val cbEquipment = binding.cbEquipment
        val cbSettleUp = binding.cbSettleUp
        val btSaveExpense = binding.saveExpense
        val btDeleteExpense = binding.deleteExpense

        val sportId = (arguments?.get("sportId") ?: "") as String
        val eventId = (arguments?.get("eventId") ?: "") as String
        val expenseId: String = (arguments?.get("expenseId") ?: "") as String

        btDeleteExpense.visibility = if(expenseId.isEmpty()) View.GONE else View.VISIBLE
        if(!UserHelper.isAdmin(requireContext())) {
            sdCategory.isEnabled = false
            etDesc.isEnabled = false
            etPrice.isEnabled = false
            etQuantity.isEnabled = false
            etFrom.isEnabled = false
            etAddedOn.isEnabled = false
            cbSettleUp.isEnabled = false
            cbEquipment.isEnabled = false
            btSaveExpense.visibility = View.INVISIBLE
            btDeleteExpense.visibility = View.GONE
        }
        etAddedOn.setText(DateHelper.toSimpleString())

        fun setCategoryAdapter(categories: List<String>) {
            val categoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
            sdCategory.setAdapter(categoryAdapter)
        }

        var memberNames: Array<String> = arrayOf()
        var fromId = ""

        membersViewModel.members.observe(viewLifecycleOwner) {
            memberNames = arrayOf()
            it.forEach { member ->
                memberNames = memberNames.plus(member.name + "(" + UserHelper.getHouse(member) + ")")
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

        btSaveExpense.setOnClickListener {
            if(expenseId.isEmpty()){
                expense = Expense()
            }
            expense.id = expenseId.ifEmpty { RandomHelper.randomUUID() }
            expense.category = sdCategory.text.toString()
            expense.description = etDesc.text.toString()
            expense.price = etPrice.text.toString().toInt()
            expense.quantity = etQuantity.text.toString().toInt()
            expense.from = fromId
            expense.addedOn = etAddedOn.text.toString()
            expense.settled = cbSettleUp.isChecked

            if(sportId.isNotEmpty()) {
                sport.expenses[expense.id] = expense
            } else {
                event.expenses[expense.id] = expense
            }

            if(cbEquipment.isChecked){
                val equipment = Equipment()
                equipment.id = RandomHelper.randomUUID()
                equipment.category = expense.category
                equipment.description = etDesc.text.toString()
                equipment.status = EquipmentStatus.Available.name
                equipment.quantity = expense.quantity
                equipment.updatedOn = etAddedOn.text.toString()
                if(sportId.isNotEmpty()) {
                    sport.equipments[equipment.id] = equipment
                }
            }

            if(sportId.isNotEmpty()) {
                sportsViewModel.save(sport)
            } else {
                eventsViewModel.save(event)
            }
            findNavController().popBackStack()
        }

        eventsViewModel.isDeleted.observe(viewLifecycleOwner) {
            if(eventsViewModel.isDeleted.value == true) {
                findNavController().popBackStack()
            }
        }

        sportsViewModel.isDeleted.observe(viewLifecycleOwner) {
            if(sportsViewModel.isDeleted.value == true) {
                findNavController().popBackStack()
            }
        }

        btDeleteExpense.setOnClickListener {
            if(sportId.isNotEmpty()) {
                sportsViewModel.deleteExpense(sportId, expenseId)
            } else {
                eventsViewModel.deleteExpense(eventId, expenseId)
            }
        }

        fun setExpenseDetails() {
            cbSettleUp.isChecked = expense.settled
            cbSettleUp.text = if(expense.settled) getString(R.string.prompt_expense_settled) else getString(R.string.prompt_expense_settle_up)
            sdCategory.setText(expense.category, false)
            etDesc.setText(expense.description)
            etPrice.setText(expense.price.toString())
            etQuantity.setText(expense.quantity.toString())

            val member = members.find { member ->
                member.id == expense.from
            } ?: User()

            etFrom.setText(getString(R.string.label_member_house, member.name, UserHelper.getHouse(member)))
            etAddedOn.setText(expense.addedOn)
        }

        if(sportId.isNotEmpty()) {
            sportsViewModel.getSport(sportId)
            sportsViewModel.getSportExpenses(sportId, expenseId)
        }
        sportsViewModel.sport.observe(viewLifecycleOwner) {
            sport = it
            setCategoryAdapter(sport.expenseCategories)
        }
        sportsViewModel.expense.observe(viewLifecycleOwner) {
            expense = it
            setExpenseDetails()
        }

        if(eventId.isNotEmpty()) {
            eventsViewModel.getEvent(eventId)
            eventsViewModel.getEventExpenses(eventId, expenseId)
        }
        eventsViewModel.event.observe(viewLifecycleOwner) {
            event = it
            setCategoryAdapter(event.expenseCategories)
        }
        eventsViewModel.expense.observe(viewLifecycleOwner) {
            expense = it
            setExpenseDetails()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}