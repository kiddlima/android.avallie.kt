package com.avallie.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    enum class CartScreen {
        PRODUCTS,
        FINISH,
        LOADING,
        SUCCESS,
        ERROR
    }

    val selectedProducts = getCart()

    val cartAdapter: CartAdapter by lazy {
        CartAdapter(context!!, selectedProducts)
    }

    var cartScreen = CartScreen.PRODUCTS

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadFragmentView()
    }

    private fun reloadFragmentView() {
        when (cartScreen) {
            CartScreen.PRODUCTS -> {
                cart_recycler.visibility = View.VISIBLE
                finish_container.visibility = View.GONE

                setCartAdapter()
            }
            CartScreen.FINISH -> {
                cart_recycler.visibility = View.GONE
                finish_container.visibility = View.VISIBLE

                setFinishInfo()
            }
            CartScreen.LOADING -> {
                finish_container.visibility = View.GONE
                loading_container.visibility = View.VISIBLE
                response_container.visibility = View.GONE

                isCancelable = false

                requestBudget()
            }
            CartScreen.SUCCESS -> {
                loading_container.visibility = View.GONE
                response_container.visibility = View.VISIBLE

                isCancelable = true

                showSuccess()
            }
            CartScreen.ERROR -> {
                loading_container.visibility = View.GONE
                response_container.visibility = View.VISIBLE

                isCancelable = true

                showFail("Erro ao se comunicar com o servidor")
            }
        }
    }

    private fun setFinishInfo() {
        btn_change_address.setOnClickListener {
            //TODO GO TO MY ACCOUNT ACTIVITY
        }

//      TODO SET ADDRESS INFO
        delivery_address_one.text = ""
        delivery_address_two.text = ""

        btn_request_budget.setOnClickListener {
            if (isFinishScreenValidate()) {
                cartScreen = CartScreen.LOADING
                reloadFragmentView()
            } else {
                Toast.makeText(context!!, getString(R.string.fill_the_fileds), Toast.LENGTH_LONG).show()
            }
        }

        back_button_finish.setOnClickListener {
            cartScreen = CartScreen.PRODUCTS
            reloadFragmentView()
        }
    }

    private fun isFinishScreenValidate(): Boolean {
        return !confirm_request_name.text.isNullOrBlank() && !confirm_dead_line.text.isNullOrBlank()
    }

    private fun requestBudget() {
        //TODO PERFORM HTTP REQUEST

        Handler().postDelayed({
            cartScreen = CartScreen.SUCCESS
            reloadFragmentView()
        }, 3000)
    }

    private fun showSuccess() {
        val accentColor = ContextCompat.getColor(context!!, R.color.colorAccent)

        response_image.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_big_check))
        response_image.setColorFilter(accentColor)
        btn_response_action.setTextColor(accentColor)
        btn_response_action.text = getString(R.string.follow_request)

        response_title.text = getString(R.string.success_exclamation)
        response_subtitle.text = getString(R.string.request_budget_success)

        btn_response_action.setOnClickListener {
            //TODO GO TO REQUEST DETAIL
        }
    }

    private fun showFail(errorMessage: String) {
        val errorColor = ContextCompat.getColor(context!!, R.color.colorError)

        response_image.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_warning))
        response_image.setColorFilter(errorColor)

        response_title.text = getString(R.string.ops_exclamation)
        response_subtitle.text = errorMessage

        btn_response_action.text = getString(R.string.try_again)
        btn_response_action.setTextColor(errorColor)

        btn_response_action.setOnClickListener {
            cartScreen = CartScreen.LOADING
            reloadFragmentView()
        }
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
                if (false) {
                    activity?.let {
                        val intent = Intent(it, LoginActivity::class.java)
                        it.startActivity(intent)
                    }
                } else {
                    cartScreen = CartScreen.FINISH
                    reloadFragmentView()
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