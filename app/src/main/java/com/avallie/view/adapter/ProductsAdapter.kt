package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.helpers.AppHelper.Companion.getPhaseByCategory
import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import kotlinx.android.synthetic.main.product_item.view.*

typealias OnProductClick = (product: Product) -> Unit

class ProductsAdapter(private val context: Context, private val products: ArrayList<Product>, private val phases: ArrayList<ConstructionPhase>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onProductClick: OnProductClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product: Product = products[position]

        holder as ProductViewHolder

        holder.productName.text = product.name
        holder.productCategorie.text = product.category

        holder.productPhaseIcon.setImageResource(
            context.resources.getIdentifier(
                "ic_phase_" + getPhaseByCategory(
                    product.category, phases
                ), "drawable", context.packageName
            )
        )
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val backgroundView: View
        val productName: TextView
        val productCategorie: TextView
        val productAddIcon: LinearLayout
        val productPhaseIcon: ImageView

        init {
            productPhaseIcon = itemView.v_product_phase_icon
            productName = itemView.v_product_name
            productCategorie = itemView.v_product_categorie
            productAddIcon = itemView.v_product_add_icon
            backgroundView = itemView

            itemView.setOnClickListener {
                onProductClick?.invoke(products[adapterPosition])
            }
        }

    }
}