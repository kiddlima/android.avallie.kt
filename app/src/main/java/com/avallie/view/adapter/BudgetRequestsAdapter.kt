package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.helpers.FormatterHelper
import com.avallie.model.BudgetRequested

typealias OnBudgetSelected = (budget: BudgetRequested) -> Unit

class BudgetRequestsAdapter(
    private val context: Context,
    private val budgetsRequest: MutableList<BudgetRequested>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onBudgetSelected: OnBudgetSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.budget_requested_item, parent, false)
        return BudgetRequestedViewHolder(view)
    }

    override fun getItemCount(): Int {
        return budgetsRequest.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BudgetRequestedViewHolder

        when (position) {
            0 -> holder.topLine.visibility = View.GONE
            budgetsRequest.size - 1 -> holder.bottomLine.visibility = View.GONE
            else -> {
                holder.bottomLine.visibility = View.VISIBLE
                holder.topLine.visibility = View.VISIBLE
            }
        }

        if (budgetsRequest.size == 1) {
            holder.bottomLine.visibility = View.GONE
            holder.topLine.visibility = View.GONE
        }

        val budget = budgetsRequest[position]

        holder.requestName.text = budget.budgetName
        holder.requestDate.text = FormatterHelper.dateFromServer(budget.budgetDate)
        holder.requestQuantity.text =
            if (budget.products?.size!! > 1) "${budget.products?.size} produtos" else "${budget.products?.size} produto"
    }

    inner class BudgetRequestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topLine = itemView.findViewById<View>(R.id.top_tracking_line)
        val bottomLine = itemView.findViewById<View>(R.id.bottom_tracking_line)
        val requestName = itemView.findViewById<TextView>(R.id.request_name)
        val requestDate = itemView.findViewById<TextView>(R.id.request_date)
        val requestQuantity = itemView.findViewById<TextView>(R.id.request_budget_size)

        init {
            itemView.setOnClickListener {
                onBudgetSelected?.invoke(budgetsRequest[adapterPosition - 1])
            }
        }
    }

}