package com.pt.mysociety.dashboard.sports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.databinding.AdapterItemBinding

class SportsAdapter: RecyclerView.Adapter<SportViewHolder>()  {
    private var sports = mutableListOf<Sport>()
    private lateinit var itemSportListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemSportListener = listener
    }

    fun setSports(sports: List<Sport>) {
        this.sports = sports.toMutableList()
        notifyItemRangeInserted(0, this.sports.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportViewHolder, position: Int) {
        val sport = sports[position]
        holder.binding.title.text = sport.name
        holder.binding.tag.text = sport.tag

        holder.itemView.setOnClickListener {
            this.itemSportListener.onItemClick(sport)
        }
    }

    override fun getItemCount(): Int {
        return sports.size
    }
}

class SportViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)