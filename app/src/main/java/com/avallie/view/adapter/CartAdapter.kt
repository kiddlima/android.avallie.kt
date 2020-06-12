package com.avallie.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.model.RequestedProduct
import com.avallie.model.request.SelectedProduct
import kotlinx.android.synthetic.main.btn_confirm_products.view.*

class CartAdapter(private val context: Context, private val requestedProducts: ArrayList<SelectedProduct>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER = 0
    private val ITEM = 1
    private val BUTTON = 2

    interface CartAdapterListener {
        fun onDeleteProduct(position: Int)
//        fun onConfirmProducts()
    }

    var cartAdapterListener: CartAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (viewType == HEADER) {
//            val view = LayoutInflater.from(context).inflate(R.layout.cart_header, parent, false)
//            CartHeaderViewHolder(view)
//        } else if (viewType == ITEM) {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(view)
//        } else {
//            val view = LayoutInflater.from(context).inflate(R.layout.btn_confirm_products, parent, false)
//            CartButtonViewHolder(view)
//        }
    }

    override fun getItemCount(): Int {
//        return requestedProducts.size + 2
        return requestedProducts.size
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (position) {
//            0 -> HEADER
//            requestedProducts.size + 1 -> BUTTON
//            else -> ITEM
//        }
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (getItemViewType(position) == HEADER) {

//            holder as CartHeaderViewHolder
//            holder.cartSize?.text = "${requestedProducts.size} produtos selecionados"

//        } else if (getItemViewType(position) == ITEM) {
//        val selectedProduct = requestedProducts[position - 1]
        val selectedProduct = requestedProducts[position]

        holder as CartItemViewHolder
        holder.producName?.text = selectedProduct.product.name.toLowerCase().capitalize()
        holder.productQuantity?.text = "${selectedProduct.amount} ${selectedProduct.product.unit}"
//        }
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var producName: TextView? = null
        var productQuantity: TextView? = null
        var deleteProduct: ImageView? = null

        init {
            producName = itemView.findViewById(R.id.selected_product_name)
            productQuantity = itemView.findViewById(R.id.selected_product_quantiy_cart)
            deleteProduct = itemView.findViewById(R.id.selected_product_delete)

            deleteProduct?.setOnClickListener {
                cartAdapterListener?.onDeleteProduct(adapterPosition)
            }
        }
    }

    inner class CartHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cartSize: TextView? = null

        init {
            cartSize = itemView.findViewById(R.id.cart_size)
        }

    }

    inner class CartButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
//            itemView.btn_confirm_products.setOnClickListener {
//                cartAdapterListener?.onConfirmProducts()
//            }
        }

    }
}