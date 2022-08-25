package com.pt.mysociety.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.pt.mysociety.BaseActivity
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.DashboardActivity
import com.pt.mysociety.data.SocietyHelper
import com.pt.mysociety.data.UserHelper
import com.pt.mysociety.databinding.ActivityProfileBinding
import com.pt.mysociety.login.model.User

class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this, ProfileViewModelFactory())[ProfileViewModel::class.java]
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModel.init(sharedPreference.getUserId())

        supportActionBar?.title = getString(R.string.title_activity_profile)

        val societyHelper = SocietyHelper()
        val wingAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            societyHelper.getWings()
        )
        binding.wing.setAdapter(wingAdapter)

        val floorAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            societyHelper.getFloors()
        )
        binding.floor.setAdapter(floorAdapter)

        binding.wing.setOnItemClickListener { _, _, position, _ ->
            setHouseAdapter(wingAdapter.getItem(position)!!)
        }

        binding.saveProfile.setOnClickListener {
            profileViewModel.save(
                User(
                    userId = sharedPreference.getUserId(),
                    email = sharedPreference.getUsername(),
                    displayName = binding.name.text.toString(),
                    wing = binding.wing.text.toString(),
                    floor = binding.floor.text.toString().toInt(),
                    house = binding.house.text.toString().toInt(),
                    role = UserHelper.roleUser,
                    updated = true
                ))
        }

        profileViewModel.profile.observe(this) {
            binding.name.setText(it.displayName.toString())
            binding.wing.setText(it.wing, false)
            setHouseAdapter(it.wing!!)
            binding.floor.setText(it.floor.toString(), false)
            binding.house.setText(it.house.toString(), false)
        }

        profileViewModel.isProfileUpdated.observe(this){
            if(it) {
                val profile = profileViewModel.profile.value
                if (profile != null) {
                    UserHelper.updateUserPref(this, profile)
                }
                startActivity(Intent(this, DashboardActivity::class.java))
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun setHouseAdapter (wing: String) {
        val houseAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            SocietyHelper().getHouse(wing)
        )
        binding.house.setAdapter(houseAdapter)
    }

}