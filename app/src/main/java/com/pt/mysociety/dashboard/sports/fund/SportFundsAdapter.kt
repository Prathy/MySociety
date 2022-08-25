package com.pt.mysociety.dashboard.sports.fund

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.dashboard.sports.Fund
import com.pt.mysociety.data.CurrencyHelper
import com.pt.mysociety.databinding.AdapterItemBinding

class SportFundsAdapter: RecyclerView.Adapter<SportFundViewHolder>()  {
    private var funds = mutableListOf<Fund>()
    private lateinit var itemSportListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemSportListener = listener
    }

    fun setSportFunds(funds: List<Fund>) {
        this.funds = funds.toMutableList()
        notifyItemRangeInserted(0, this.funds.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportFundViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportFundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportFundViewHolder, position: Int) {
        val fund = funds[position]
        holder.binding.title.text = fund.from
        holder.binding.tag.text = holder.itemView.context.getString(R.string.item_contribution, CurrencyHelper.convertToRupees(fund.amount))

        holder.itemView.setOnClickListener {
            this.itemSportListener.onItemClick(fund)
        }
    }

    override fun getItemCount(): Int {
        return funds.size
    }
}

class SportFundViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)