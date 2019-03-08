package com.avallie.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.avallie.R
import com.avallie.helpers.PaperHelper.Companion.getPhases
import com.avallie.model.ConstructionPhase
import com.avallie.model.Product
import com.avallie.view.FiltersActivity
import com.avallie.view.adapter.ActiveFiltersAdapter
import com.avallie.view.adapter.ProductsAdapter
import com.avallie.webservice.ConnectionListener
import com.avallie.webservice.HttpService
import kotlinx.android.synthetic.main.products_fragment.*

class ProductsFragment : Fragment() {

    private var products: ArrayList<Product> = ArrayList()
    private var phases: ArrayList<ConstructionPhase> = getPhases()

    private var selectedCategories: ArrayList<String> = ArrayList()
    private var productNameSearched: String? = null

    private val productsAdapter: ProductsAdapter by lazy {
        ProductsAdapter(context!!, products, phases)
    }

    private val categoriesAdapter: ActiveFiltersAdapter by lazy {
        ActiveFiltersAdapter(context!!, selectedCategories)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCategories.clear()
        selectedCategories.addAll(arguments?.getStringArrayList("categories").orEmpty())

        getProducts()

        setCategoriesAdapter()

        v_filter_icon.setOnClickListener {
            Intent(context!!, FiltersActivity::class.java).run {
                putExtra("categories", selectedCategories)
                startActivityForResult(this, 1)
            }
        }

//        v_products_recycler.setOnTouchListener { v, event ->
//            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(v.windowToken, 0)
//            v.requestFocus()
//        }
    }

    private fun getProducts() {
        HttpService(context!!).getProducts(
            selectedCategories,
            productNameSearched,
            object : ConnectionListener<ArrayList<Product>> {
                override fun onSuccess(response: ArrayList<Product>) {
                    products.clear()
                    products.addAll(response)

                    setProductAdapter()
                }

                override fun onFail(error: String?) {
                    //TODO SHOW ERROR MESSAGE
                }

                override fun noInternet() {
                    //TODO SHOW NO INTERNET MESSAGE
                }

            })
    }

    private fun setCategoriesAdapter() {
        v_active_filter_recycler.adapter = categoriesAdapter
        categoriesAdapter.onCategoryDeleted = {
            selectedCategories.remove(it)

            categoriesAdapter.notifyDataSetChanged()

            //TODO RE-DO REQUEST
        }

        v_active_filter_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setProductAdapter() {
        v_products_recycler.adapter = productsAdapter
        productsAdapter.onProductClick = {
            AddProductDialog(context!!, it).showDialog()
        }

        v_products_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }


}