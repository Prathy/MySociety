package com.pt.mysociety.dashboard.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.dashboard.BaseFragment
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.data.RandomHelper
import com.pt.mysociety.data.SocietyHelper
import com.pt.mysociety.data.UserHelper
import com.pt.mysociety.databinding.FragmentMemberDetailsBinding
import com.pt.mysociety.login.model.User

class MemberDetailsFragment : BaseFragment() {

    private var _binding: FragmentMemberDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var member: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMemberDetailsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val etName: EditText = binding.name
        val etWing: AutoCompleteTextView = binding.addHouseContainer.wing
        val etFloor: AutoCompleteTextView = binding.addHouseContainer.floor
        val etHouse: AutoCompleteTextView = binding.addHouseContainer.house
        val etEmail: EditText = binding.email
        val etContact: EditText = binding.contact
        val btUpdateMember = binding.updateMember

        val memberId: String? = arguments?.get("memberId") as String?
        if(!UserHelper.isAdmin(requireContext())) {
            etName.isEnabled = false
            etWing.isEnabled = false
            etFloor.isEnabled = false
            etHouse.isEnabled = false
            etEmail.isEnabled = false
            etContact.isEnabled = false
            btUpdateMember.visibility = View.INVISIBLE
        }

        val societyHelper = SocietyHelper()
        val wingAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            societyHelper.getWings()
        )
        binding.addHouseContainer.wing.setAdapter(wingAdapter)

        val floorAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            societyHelper.getFloors()
        )
        binding.addHouseContainer.floor.setAdapter(floorAdapter)

        binding.addHouseContainer.wing.setOnItemClickListener { _, _, position, _ ->
            setHouseAdapter(wingAdapter.getItem(position)!!)
        }

        btUpdateMember.setOnClickListener {
            if(memberId.isNullOrEmpty()){
                member = User()
            }
            member.id = memberId ?: RandomHelper.randomUUID()
            member.name = etName.text.toString()
            member.wing = etWing.text.toString()
            member.floor = etFloor.text.toString().toInt()
            member.house = etHouse.text.toString().toInt()
            member.email = etEmail.text.toString()
            member.contact = etContact.text.toString()

            membersViewModel.save(member)
            findNavController().popBackStack()
        }

        membersViewModel.members.observe(viewLifecycleOwner) {
            it.forEach { _member ->
                if(_member.id == memberId) {
                    member = _member
                    etName.setText(member.name.toString())
                    etWing.setText(member.wing.toString(), false)
                    etFloor.setText(member.floor.toString(), false)
                    etHouse.setText(member.house.toString(), false)
                    etEmail.setText(member.email.toString())
                    etContact.setText(member.contact.toString())
                    setHouseAdapter(member.wing.toString())
                }
            }
        }

        return root
    }

    private fun setHouseAdapter (wing: String) {
        val houseAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            SocietyHelper().getHouse(wing)
        )
        binding.addHouseContainer.house.setAdapter(houseAdapter)
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