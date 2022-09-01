package com.pt.mysociety.dashboard.member

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.data.UserHelper
import com.pt.mysociety.databinding.AdapterItemBinding
import com.pt.mysociety.login.model.User
import java.util.*
import kotlin.collections.ArrayList

class MembersAdapter: RecyclerView.Adapter<MemberViewHolder>(), Filterable  {

    var membersFilterList = ArrayList<User>()
    private var members = mutableListOf<User>()
    private lateinit var itemMemberListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemMemberListener = listener
    }

    fun setMembers(funds: List<User>) {
        this.members = funds.toMutableList()
        this.membersFilterList = members as ArrayList<User>
        notifyItemRangeInserted(0, this.membersFilterList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = membersFilterList[position]
        holder.binding.no.visibility = View.VISIBLE
        holder.binding.no.text = holder.itemView.context.getString(R.string.info_number, position + 1)
        holder.binding.title.text = member.name
        holder.binding.tag.text = holder.itemView.context.getString(R.string.info_house, UserHelper.getHouse(member))

        holder.itemView.setOnClickListener {
            this.itemMemberListener.onItemClick(member)
        }
    }

    override fun getItemCount(): Int {
        return membersFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                membersFilterList = if (charSearch.isEmpty()) {
                    members as ArrayList<User>
                } else {
                    val resultList = ArrayList<User>()
                    for (row in members) {
                        if (row.name?.lowercase(Locale.ROOT)
                                ?.contains(constraint.toString().lowercase(Locale.ROOT)) == true) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = membersFilterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                membersFilterList = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
}

class MemberViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)