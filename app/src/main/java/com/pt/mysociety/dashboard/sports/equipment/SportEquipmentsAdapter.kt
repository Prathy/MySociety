package com.pt.mysociety.dashboard.sports.equipment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.sports.Equipment
import com.pt.mysociety.databinding.AdapterItemBinding

class SportEquipmentsAdapter: RecyclerView.Adapter<SportEquipmentViewHolder>()  {
    private var equipments = mutableListOf<Equipment>()

    fun setSportEquipments(equipments: List<Equipment>) {
        this.equipments = equipments.toMutableList()
        notifyItemRangeInserted(0, this.equipments.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportEquipmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportEquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportEquipmentViewHolder, position: Int) {
        val equipment = equipments[position]
        holder.binding.title.text = equipment.description
        holder.binding.tag.text = holder.itemView.context.getString(R.string.item_quantity, equipment.quantity)
    }

    override fun getItemCount(): Int {
        return equipments.size
    }
}

class SportEquipmentViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)