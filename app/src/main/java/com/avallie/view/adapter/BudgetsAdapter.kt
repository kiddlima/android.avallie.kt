package com.avallie.view.adapter

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.helpers.FormatterHelper.Companion.toCurrency
import com.avallie.model.Budget

class BudgetsAdapter(private val context: Context, private val budgets: MutableList<Budget>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BudgetViewHolder(LayoutInflater.from(context).inflate(R.layout.budget_detail_item, parent, false))
    }

    override fun getItemCount(): Int = budgets.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BudgetViewHolder

        val budget = budgets[position]

        val background = if (position % 2 == 0) R.drawable.budget_blue_shape else R.drawable.budget_green_shape

        holder.headerContainer.background = ContextCompat.getDrawable(context, background)

        holder.supplierName.text = budget.supplierName
        holder.totalPrice.text = toCurrency(budget.totalPrice)
        holder.paymentOption.text = budget.paymentOption
        holder.addressOption.text =
            budget.deliveryOption + "\n Frete: ${toCurrency(budget.shippingPrice)} (já incluso) - ${budget.productStatus}"
    }

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val supplierName: TextView = itemView.findViewById(R.id.budget_detail_supplier_name)

        private val paymentContainer: ConstraintLayout =
            itemView.findViewById(R.id.budget_detail_payment_info_container)
        private val deliveryContainer: ConstraintLayout =
            itemView.findViewById(R.id.budget_detail_address_info_container)

        val paymentOption: TextView = this.paymentContainer.findViewById(R.id.second_line)
        val addressOption: TextView = this.deliveryContainer.findViewById(R.id.second_line)
        val totalPrice: TextView = itemView.findViewById(R.id.budget_detail_final_price)
        val headerContainer: ConstraintLayout = itemView.findViewById(R.id.header_container)

        init {
            paymentContainer.findViewById<TextView>(R.id.first_line).text =
                context.getString(R.string.payment_type_label)
            deliveryContainer.findViewById<TextView>(R.id.first_line).text = context.getString(R.string.delivery_label)
            paymentContainer.findViewById<ImageView>(R.id.icon)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_credit_cards))
            deliveryContainer.findViewById<ImageView>(R.id.icon)
                .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delivery))
        }
    }
}