package com.pt.mysociety.dashboard.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.databinding.AdapterItemBinding

class EventsAdapter: RecyclerView.Adapter<EventViewHolder>()  {
    private var events = mutableListOf<Event>()
    private lateinit var itemEventListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemEventListener = listener
    }

    fun setEvents(events: List<Event>) {
        this.events = events.toMutableList()
        notifyItemRangeInserted(0, this.events.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.binding.title.text = event.name

        holder.itemView.setOnClickListener {
            this.itemEventListener.onItemClick(event)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}

class EventViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)