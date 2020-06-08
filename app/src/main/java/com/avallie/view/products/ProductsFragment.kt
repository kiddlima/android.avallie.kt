package com.avallie.view.products

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.databinding.FragmentProductsBinding
import com.avallie.helpers.PaperHelper.Companion.getPhases
import com.avallie.model.ConstructionPhase
import com.avallie.model.ScreenState.*
import com.avallie.view.adapter.ActiveFiltersAdapter
import com.avallie.view.adapter.ProductsAdapter
import com.avallie.view.filter.FiltersActivity
import com.avallie.view.fragment.AddProductDialog


class ProductsFragment : Fragment() {

    private var phases: ArrayList<ConstructionPhase> = getPhases()

    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter(context!!, emptyList(), ArrayList(), phases)
    }

    private val categoriesAdapter: ActiveFiltersAdapter by lazy {
        ActiveFiltersAdapter(context!!, viewModel.categories.value!!)
    }

    lateinit var viewModel: ProductsViewModel

    lateinit var binding: FragmentProductsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, com.avallie.R.layout.fragment_products, container, false)
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

        viewModel.loadProducts(context!!)

        setObservers()

        setCategoriesAdapter()
        setProductAdapter()

        binding.vFilterIcon.setOnClickListener {
            Intent(context!!, FiltersActivity::class.java).run {
                putExtra("categories", viewModel.categories.value)
                startActivityForResult(this, 1)
            }
        }

        binding.vProductsRecycler.setOnTouchListener { v, event ->
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(v.windowToken, 0)

            false
        }
    }

    private fun setObservers() {
        viewModel.products.observe(this, Observer {
            productsAdapter.update(it)
        })
    }

    private fun setCategoriesAdapter() {
        binding.vActiveFilterRecycler.adapter = categoriesAdapter
        categoriesAdapter.onCategoryDeleted = {
            viewModel.categories.value?.remove(it)

            viewModel.screenState.value = Loading

            categoriesAdapter.notifyDataSetChanged()

            viewModel.loadProducts(context!!)
        }

        binding.vActiveFilterRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setProductAdapter() {
        binding.vProductsRecycler.adapter = productsAdapter
        productsAdapter.onProductClick = {
            AddProductDialog(context!!, it).showDialog()
        }

        binding.vProductsRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.searchUser.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productsAdapter.filter.filter(s) {
                    if (it == 0) {
                        viewModel.screenState.value = NoData
                    } else {
                        viewModel.screenState.value = Success
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }


}