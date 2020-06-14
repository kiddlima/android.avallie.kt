package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.avallie.helpers.AppHelper.Companion.getPhaseByCategory
import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import kotlinx.android.synthetic.main.product_item.view.*


typealias OnProductClick = (product: Product) -> Unit

class ProductsAdapter(
    private val context: Context,
    private var products: List<Product>,
    private var filteredProducts: ArrayList<Product>,
    private val phases: ArrayList<ConstructionPhase>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                val newListProducts = ArrayList<Product>()

                if (charString.isNotEmpty() && charSequence.length >= 3) {
                    products.forEach {
                        if (it.name.contains(charString, true)) {
                            newListProducts.add(it)
                        }
                    }
                } else {
                    newListProducts.addAll(products)
                }

                val filterResults = FilterResults()
                filterResults.values = newListProducts
                filterResults.count = newListProducts.size
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredProducts = filterResults.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }

    var onProductClick: OnProductClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(com.avallie.R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredProducts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product: Product = filteredProducts[position]

        holder as ProductViewHolder

        holder.productName.text = product.name.toLowerCase().capitalize()
        holder.productCategorie.text = product.category.toLowerCase().capitalize()

    }

    fun update(newProducts: List<Product>) {
        this.products = newProducts

        this.filteredProducts.clear()
        this.filteredProducts.addAll(newProducts)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productName: TextView = itemView.v_product_name
        val productCategorie: TextView = itemView.v_product_categorie

        init {
            itemView.setOnClickListener {
                onProductClick?.invoke(filteredProducts[adapterPosition])
            }
        }

    }
}