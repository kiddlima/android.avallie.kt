package com.avallie.view.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.helpers.AuthHelper
import com.avallie.helpers.FormatterHelper.Companion.stringToDate
import com.avallie.helpers.PaperHelper
import com.avallie.helpers.PaperHelper.Companion.getCart
import com.avallie.model.request.BudgetRequest
import com.avallie.view.LoginActivity
import com.avallie.view.MainActivity
import com.avallie.view.adapter.CartAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import java.text.SimpleDateFormat
import java.util.*


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

    lateinit var viewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }

        viewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        viewModel.cartScreenState.value = CartScreen.PRODUCTS

        confirm_dead_line.run {
            val listener = MaskedTextChangedListener("[00]{/}[00]{/}[0000]", confirm_dead_line)

            addTextChangedListener(listener)
            onFocusChangeListener = listener
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.cartScreenState.observe(this, Observer { cartScreen ->
            when (cartScreen) {
                CartScreen.PRODUCTS -> {
                    cart_container.visibility = View.VISIBLE
                    finish_container.visibility = View.GONE

                    setCartAdapter()
                }
                CartScreen.FINISH -> {
                    cart_container.visibility = View.GONE
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

                    PaperHelper.clearCart()

                    showSuccess()
                }
                CartScreen.ERROR -> {
                    loading_container.visibility = View.GONE
                    response_container.visibility = View.VISIBLE

                    isCancelable = true

                    showFail("Erro ao se comunicar com o servidor")
                }
            }
        })
    }

    private fun returnsNullIfValid(): String? {
        if (confirm_request_name.text.isNullOrBlank()
            || confirm_dead_line.text.isNullOrBlank()
        ) {
            return "Preencha os camos acima"
        }

        val month = confirm_dead_line.text?.substring(3, 5)?.toInt()
        val day = confirm_dead_line.text?.substring(0, 2)?.toInt()
        val date = stringToDate(confirm_dead_line.text.toString(), "dd/MM/yyyy")

        if (month!! > 12) {
            return "Mês inválido"
        }

        if (day!! > 31) {
            return "Dia inválido"
        }

        if (date.before(Date())) {
            return "O prazo de entrega é anterior ao dia atual"
        }

        return null
    }

    private fun setFinishInfo() {
        val customer = PaperHelper.getCustomer()

        delivery_address_one.text =
            "${customer?.street}, ${customer?.streetNumber} - ${customer?.zipCode}"
        delivery_address_two.text = "${customer?.city}, ${customer?.state}"

        btn_request_budget.setOnClickListener {
            val errorMessage = returnsNullIfValid()

            if (errorMessage == null) {
                viewModel.cartScreenState.value = CartScreen.LOADING
            } else {
                Toast.makeText(context!!, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        back_button_finish.setOnClickListener {
            viewModel.cartScreenState.value = CartScreen.PRODUCTS
        }
    }

    private fun requestBudget() {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var formattedDateString = ""

        formatter.parse(confirm_dead_line.text.toString()).run {
            formattedDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
        }

        val budgetRequest = BudgetRequest(
            confirm_request_name.text.toString(),
            formattedDateString,
            "${delivery_address_one.text} ${delivery_address_two.text}",
            getCart()
        )

        viewModel.requestBudget(budgetRequest, context!!)
    }

    private fun showSuccess() {
        val accentColor = ContextCompat.getColor(context!!, R.color.colorAccent)

        animation_view.run {
            speed = 0.8f
            playAnimation()
        }

        response_image.visibility = View.GONE
        btn_response_action.setTextColor(accentColor)
        btn_response_action.text = getString(R.string.follow_request)

        response_title.text = getString(R.string.success_exclamation)
        response_subtitle.text = getString(R.string.request_budget_success)

        btn_response_action.setOnClickListener {
            dismiss()

            (activity as MainActivity).openBudgetsSheet(viewModel.budgetRequested.value!!.id, null)
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
            viewModel.cartScreenState.value = CartScreen.LOADING
        }
    }

    private fun setCartAdapter() {
        cart_recycler.adapter = cartAdapter

        cart_size.text = "${selectedProducts.size} produtos selecionados"

        btn_confirm_products.setOnClickListener {
            if (!AuthHelper.isLoggedIn()) {
                activity?.let {
                    val intent = Intent(it, LoginActivity::class.java)
                    it.startActivity(intent)
                }
            } else {
                viewModel.cartScreenState.value = CartScreen.FINISH
            }
        }

        cartAdapter.cartAdapterListener = (object : CartAdapter.CartAdapterListener {
            override fun onDeleteProduct(position: Int) {
                selectedProducts.remove(selectedProducts[position])
                PaperHelper.updateCart(selectedProducts)

                cartAdapter.notifyDataSetChanged()

                if (selectedProducts.isEmpty()) {
                    dismiss()
                }
            }
        })

        cart_recycler.layoutManager =
            LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        (activity as MainActivity).updateSelectedItem()
        (activity as MainActivity).updateCartBadge()
    }
}