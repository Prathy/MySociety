package com.pt.mysociety.dashboard.fund

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.data.CurrencyHelper
import com.pt.mysociety.databinding.AdapterItemBinding
import java.util.*
import kotlin.collections.ArrayList

class SportFundsAdapter: RecyclerView.Adapter<SportFundViewHolder>(), Filterable {

    var fundsFilterList = ArrayList<Fund>()
    private var funds = mutableListOf<Fund>()
    private lateinit var itemSportListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemSportListener = listener
    }

    fun setSportFunds(funds: List<Fund>) {
        this.funds = funds.toMutableList()
        this.fundsFilterList = funds as ArrayList<Fund>
        notifyItemRangeInserted(0, this.funds.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportFundViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportFundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportFundViewHolder, position: Int) {
        val fund = fundsFilterList[position]
        holder.binding.no.visibility = View.VISIBLE
        holder.binding.no.text = holder.itemView.context.getString(R.string.info_number, position + 1)
        holder.binding.title.text = fund.from
        holder.binding.tag.text = holder.itemView.context.getString(R.string.item_contribution, CurrencyHelper.convertToRupees(fund.amount))

        holder.itemView.setOnClickListener {
            this.itemSportListener.onItemClick(fund)
        }
    }

    override fun getItemCount(): Int {
        return fundsFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                fundsFilterList = if (charSearch.isEmpty()) {
                    funds as ArrayList<Fund>
                } else {
                    val resultList = ArrayList<Fund>()
                    for (row in funds) {
                        if (row.from.lowercase(Locale.ROOT)
                                .contains(constraint.toString().lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = fundsFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                fundsFilterList = results?.values as ArrayList<Fund>
                notifyDataSetChanged()
            }
        }
    }
}

class SportFundViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)