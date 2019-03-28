package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.model.BudgetRequested

class BudgetRequestsAdapter(private val context: Context, private val budgetsRequested: ArrayList<BudgetRequested>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val item = 0
    private val header = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            item -> {
                val view = LayoutInflater.from(context).inflate(R.layout.budget_requested_item, parent, false)
                BudgetRequestedViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.budget_requested_header, parent, false)
                BudgetRequestedViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return budgetsRequested.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            item -> {
                holder as BudgetRequestedViewHolder

                when (position) {
                    1 -> holder.topLine.visibility = View.GONE
                    budgetsRequested.size -> holder.bottomLine.visibility = View.GONE
                    else -> {
                        holder.bottomLine.visibility = View.VISIBLE
                        holder.topLine.visibility = View.VISIBLE
                    }
                }

                val budget = budgetsRequested[position - 1]

                holder.requestName.text = budget.budgetName
                holder.requestDate.text = budget.budgetDate.toString()
                holder.requestQuantity.text = if (budget.budgetsAvaiable > 1) "${budget.budgetsAvaiable} orçamentos" else "${budget.budgetsAvaiable} orçamento"

            }
            else -> {

            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) header else item
    }

    inner class BudgetRequestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topLine = itemView.findViewById<View>(R.id.top_tracking_line)
        val bottomLine = itemView.findViewById<View>(R.id.bottom_tracking_line)
        val requestName = itemView.findViewById<TextView>(R.id.request_name)
        val requestDate = itemView.findViewById<TextView>(R.id.request_date)
        val requestQuantity = itemView.findViewById<TextView>(R.id.request_budget_size)
    }

    inner class BudgetRequestedHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}