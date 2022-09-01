package com.pt.mysociety.dashboard.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class MembersViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MembersViewModel::class.java)) {
            return MembersViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}