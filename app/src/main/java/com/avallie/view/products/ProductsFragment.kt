package com.avallie.view.products

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.databinding.FragmentProductsBinding
import com.avallie.helpers.AppHelper.Companion.hideKeyboard
import com.avallie.model.ScreenState.Loading
import com.avallie.view.MainActivity
import com.avallie.view.adapter.ActiveFiltersAdapter
import com.avallie.view.filter.FiltersActivity
import com.avallie.widgets.NoDataContainer


class ProductsFragment : Fragment() {

    lateinit var productsAdapter: ProductsPagedAdapter

    private val categoriesAdapter: ActiveFiltersAdapter by lazy {
        ActiveFiltersAdapter(context!!, viewModel.categories.value!!)
    }

    lateinit var viewModel: ProductsViewModel

    lateinit var binding: FragmentProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            com.avallie.R.layout.fragment_products,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = this

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.categories.value = arguments?.getStringArrayList("categories")

        setCategoriesAdapter()
        setProductAdapter()

        loadProducts()

        binding.vFilterIcon.setOnClickListener {
            Intent(context!!, FiltersActivity::class.java).run {
                putExtra("categories", viewModel.categories.value)
                startActivityForResult(this, 1)
            }
        }

        binding.searchUser.setOnEditorActionListener { v, actionId, event ->
            loadProducts()
            hideKeyboard(context!!, view)
            true
        }

        binding.noDataContainer =
            NoDataContainer("Nenhum produto encontrado", "Tente procurar por outro nome", true)


        binding.vProductsRecycler.setOnTouchListener { v, event ->
            hideKeyboard(context!!, view)

            false
        }
    }


    fun loadProducts() {
        productsAdapter.submitList(null)

        viewModel.loadProducts(
            context!!,
            ProductsQuery(0, 20, viewModel.categories.value!!, viewModel.productSearchName.value!!)
        )

        setObservers()
    }

    private fun setObservers() {
        viewModel.itemPagedList?.observe(this, Observer {
            productsAdapter.submitList(it)
        })
    }

    private fun setCategoriesAdapter() {
        binding.vActiveFilterRecycler.adapter = categoriesAdapter
        categoriesAdapter.onCategoryDeleted = {
            viewModel.categories.value?.remove(it)

            viewModel.screenState.value = Loading

            categoriesAdapter.notifyDataSetChanged()

            loadProducts()
        }

        binding.vActiveFilterRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setProductAdapter() {
        binding.vProductsRecycler.itemAnimator = null

        productsAdapter = ProductsPagedAdapter(context!!, onProductClick = {
            (activity as MainActivity).openAddProduct(it)
        })

        binding.vProductsRecycler.adapter = productsAdapter

        binding.vProductsRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.searchUser.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(name: Editable?) {
                if (name!!.length >= 3) {
                    viewModel.productSearchName.value = name.toString()

                    loadProducts()
                } else if (name.isEmpty()) {
                    viewModel.productSearchName.value = ""

                    loadProducts()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }
}

