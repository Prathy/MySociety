package com.pt.mysociety.dashboard.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pt.mysociety.R
import com.pt.mysociety.SharedPreference
import com.pt.mysociety.databinding.FragmentEventDetailsBinding

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val eventsViewModel = ViewModelProvider(this, EventsViewModelFactory())[EventsViewModel::class.java]

        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val etProvider: EditText = binding.name
        val btCreateEvent = binding.createEvent

        @Suppress("UNCHECKED_CAST")
        event = if(arguments?.get("event") != null) Event().fromMap(arguments?.get("event") as Map<String, String>) else Event()
        if(event.id.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            etProvider.setText(event.name)
            btCreateEvent.text = getString(R.string.action_save)
        } else {
            btCreateEvent.text = getString(R.string.action_create)
        }

        btCreateEvent.setOnClickListener {
            event.name = etProvider.text.toString()
            event.ownerId = SharedPreference(this.requireContext()).getUserId()
            eventsViewModel.save(event)
            findNavController().popBackStack()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}