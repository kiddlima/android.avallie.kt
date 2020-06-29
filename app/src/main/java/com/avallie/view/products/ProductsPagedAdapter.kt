package com.avallie.view.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.model.Product
import kotlinx.android.synthetic.main.product_item.view.*

typealias OnProductClick = (product: Product) -> Unit

class ProductsPagedAdapter(
    private val context: Context,
    private val onProductClick: OnProductClick
) : PagedListAdapter<Product, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.equals(newItem)
    }

}) {

    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product: Product = getItem(position)!!

        holder as ProductsPagedAdapter.ProductViewHolder

        holder.productName.text = product.name.toLowerCase().capitalize()
        holder.productCategorie.text = product.category.toLowerCase().capitalize()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.v_product_name
        val productCategorie: TextView = itemView.v_product_categorie

        init {
            itemView.setOnClickListener {
                onProductClick.invoke(getItem(adapterPosition)!!)
            }
        }
    }
}

