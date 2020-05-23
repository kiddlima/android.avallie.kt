package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.model.RequestedProduct
import com.avallie.model.request.SelectedProduct
import java.util.*

typealias OnProductSelected = (requestedProduct: RequestedProduct) -> Unit

class BudgetProductsAdapter(private val context: Context, private val products: MutableList<RequestedProduct>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val header = 0
    private val item = 1

    var onProductSelectedProduct: OnProductSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.budget_request_product_header, parent, false)
                BudgetProductHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.budget_request_product_item, parent, false)
                BudgetProductViewHolder(view)
            }
        }

    }

    override fun getItemCount(): Int {
        return products.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            header
        } else {
            item
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) != 0) {
            holder as BudgetProductViewHolder

            val product = products[position - 1]

            holder.productName.text = product.name
            holder.productQuantity.text = "${product.amount} ${product.unit}"

            if (product.budgetsAvaiable!! > 0) {
                holder.productBudgetAvaiable.text = "${product.budgetsAvaiable.toString()} disponíveis"
                holder.productBudgetAvaiable.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            } else {
                holder.productBudgetAvaiable.text = context.getString(R.string.no_avaiable_budgets)
            }
        }
    }

    inner class BudgetProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName = itemView.findViewById<TextView>(R.id.budget_product_name)
        var productQuantity = itemView.findViewById<TextView>(R.id.budget_product_quantity)
        var productBudgetAvaiable = itemView.findViewById<TextView>(R.id.budget_product_budget_avaiable)

        init {
            itemView.setOnClickListener {
                onProductSelectedProduct?.invoke(products[adapterPosition - 1])
            }
        }
    }

    inner class BudgetProductHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}