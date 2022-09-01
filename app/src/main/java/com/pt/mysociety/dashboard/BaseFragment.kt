package com.pt.mysociety.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pt.mysociety.SharedPreference
import com.pt.mysociety.dashboard.member.MembersViewModel
import com.pt.mysociety.dashboard.member.MembersViewModelFactory
import com.pt.mysociety.login.model.User

open class BaseFragment : Fragment() {

    lateinit var membersViewModel: MembersViewModel
    var members: Array<User> = arrayOf()
    protected lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreference = SharedPreference(requireContext())
        membersViewModel = ViewModelProvider(this, MembersViewModelFactory())[MembersViewModel::class.java]
        membersViewModel.init()

        membersViewModel.members.observe(viewLifecycleOwner) {
            members = arrayOf()
            it.forEach { member ->
                members = members.plus(member)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

}