package com.avallie.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.helpers.PaperHelper
import com.avallie.helpers.PaperHelper.Companion.getCart
import com.avallie.view.LoginActivity
import com.avallie.view.MainActivity
import com.avallie.view.adapter.CartAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : BottomSheetDialogFragment() {

    val selectedProducts = getCart()

    val cartAdapter: CartAdapter by lazy {
        CartAdapter(context!!, selectedProducts)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCartAdapter()
    }

    private fun setCartAdapter() {
        cart_recycler.adapter = cartAdapter
        cartAdapter.cartAdapterListener = (object : CartAdapter.CartAdapterListener {
            override fun onDeleteProduct(position: Int) {
                selectedProducts.remove(selectedProducts[position - 1])
                PaperHelper.updateCart(selectedProducts)

                cartAdapter.notifyDataSetChanged()

                if (selectedProducts.isEmpty()) {
                    dismiss()
                }
            }

            override fun onConfirmProducts() {
                //TODO CHECK IF IS LOGGED AND DO LOGIC
                activity?.let{
                    val intent = Intent (it, LoginActivity::class.java)
                    it.startActivity(intent)
                }
            }
        })

        cart_recycler.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        (activity as MainActivity).updateSelectedItem()
        (activity as MainActivity).updateCartBadge()
    }
}