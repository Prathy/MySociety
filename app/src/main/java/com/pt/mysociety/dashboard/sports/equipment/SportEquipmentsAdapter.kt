package com.pt.mysociety.dashboard.sports.equipment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.sports.Equipment
import com.pt.mysociety.dashboard.sports.EquipmentStatus
import com.pt.mysociety.helpers.DateHelper
import com.pt.mysociety.databinding.AdapterItemBinding

class SportEquipmentsAdapter: RecyclerView.Adapter<SportEquipmentViewHolder>()  {
    private var equipments = mutableListOf<Equipment>()

    fun setSportEquipments(equipments: List<Equipment>) {
        this.equipments = equipments.sortedByDescending {
            DateHelper.toDate(it.updatedOn)
        }.toMutableList()
        notifyItemRangeInserted(0, this.equipments.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportEquipmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportEquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportEquipmentViewHolder, position: Int) {
        val equipment = equipments[position]
        holder.binding.no.visibility = View.VISIBLE
        holder.binding.no.text = holder.itemView.context.getString(R.string.info_number, position + 1)
        holder.binding.title.text = equipment.description
        holder.binding.tag.text = holder.itemView.context.getString(R.string.item_status, equipment.status, DateHelper.toNormalize(equipment.updatedOn))
        holder.binding.tag.setTextColor(ContextCompat.getColor(holder.itemView.context, if(equipment.status == EquipmentStatus.Available.name) android.R.color.holo_green_dark else android.R.color.holo_red_dark))
        holder.binding.tag2.text = holder.itemView.context.getString(R.string.item_quantity, equipment.quantity)
    }

    override fun getItemCount(): Int {
        return equipments.size
    }
}

class SportEquipmentViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)