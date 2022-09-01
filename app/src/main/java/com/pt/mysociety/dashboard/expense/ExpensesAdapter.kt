package com.pt.mysociety.dashboard.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pt.mysociety.R
import com.pt.mysociety.dashboard.AdapterItemEventListener
import com.pt.mysociety.data.CurrencyHelper
import com.pt.mysociety.databinding.AdapterItemBinding

class SportExpensesAdapter: RecyclerView.Adapter<SportExpenseViewHolder>()  {
    private var expenses = mutableListOf<Expense>()
    private lateinit var itemSportListener: AdapterItemEventListener

    fun setListener(listener: AdapterItemEventListener) {
        this.itemSportListener = listener
    }

    fun setSportExpenses(expenses: List<Expense>) {
        this.expenses = expenses.toMutableList()
        notifyItemRangeInserted(0, this.expenses.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = AdapterItemBinding.inflate(inflater, parent, false)
        return SportExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.no.visibility = View.VISIBLE
        holder.binding.no.text = holder.itemView.context.getString(R.string.info_number, position + 1)
        holder.binding.title.text = expense.description
        holder.binding.tag.text = holder.itemView.context.getString(R.string.item_price,
            CurrencyHelper.convertToRupees(expense.price),
            expense.quantity,
            CurrencyHelper.convertToRupees(expense.price * expense.quantity)
        )

        holder.itemView.setOnClickListener {
            this.itemSportListener.onItemClick(expense)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}

class SportExpenseViewHolder(val binding: AdapterItemBinding) : RecyclerView.ViewHolder(binding.root)