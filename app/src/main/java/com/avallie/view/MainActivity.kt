package com.avallie.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.aurelhubert.ahbottomnavigation.notification.AHNotification
import com.avallie.R
import com.avallie.helpers.AuthHelper
import com.avallie.helpers.PaperHelper.Companion.getCart
import com.avallie.model.BudgetNotificationData
import com.avallie.view.fragment.BudgetRequestsFragment
import com.avallie.view.fragment.CartFragment
import com.avallie.view.products.ProductsFragment
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : AppCompatActivity() {

    private var categories: ArrayList<String> = ArrayList()

    private var lastItemSelected: Int? = null

    private var productsFragment: ProductsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent.getStringExtra("budgetId")?.let { budgetId ->
            intent.getStringExtra("selectedProductId")?.let { selectedProductId ->
                openBudgetsSheet(budgetId.toLong(), selectedProductId.toLong())
            }
        }

        createMenu()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.getStringExtra("budgetId")?.let { budgetId ->
            intent.getStringExtra("selectedProductId")?.let { selectedProductId ->
                openBudgetsSheet(budgetId.toLong(), selectedProductId.toLong())
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lastItemSelected = 0
        updateSelectedItem()
    }

    private fun goToProducts() {
        lastItemSelected = 0

        val bundle = Bundle()
        productsFragment = ProductsFragment()

        bundle.putStringArrayList("categories", categories)
        productsFragment?.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.container, productsFragment!!)
            .commit()
    }

    private fun openCartSheet() {
        val cartFragment = CartFragment()

        cartFragment.show(supportFragmentManager, "cartSheet")
    }

    fun updateSelectedItem() {
        bottom_navigation.currentItem = lastItemSelected!!
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 1) {
            categories.clear()
            categories.addAll(data?.getStringArrayListExtra("categories").orEmpty())

            goToProducts()
        }
    }

    private fun createMenu() {
        bottom_navigation.run {
            addItem(AHBottomNavigationItem(getString(R.string.products), R.drawable.ic_products))
            addItem(AHBottomNavigationItem(getString(R.string.cart), R.drawable.ic_cart))
            addItem(AHBottomNavigationItem(getString(R.string.budget), R.drawable.ic_list))
            addItem(AHBottomNavigationItem(getString(R.string.account), R.drawable.ic_account))

            accentColor = ContextCompat.getColor(context, R.color.colorAccent)
            setNotificationBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
            setNotificationTextColor(Color.WHITE)

            setUseElevation(true)

            setOnTabSelectedListener { position, wasSelected ->
                when (position) {
                    0 -> {
                        if (!isFragmentVisible(productsFragment)) {
                            goToProducts()
                        }
                    }
                    1 -> openCartSheet()
                    2 -> openBudgetsSheet(null, null)
                    3 -> openAccount()
                }

                true
            }

            currentItem = 0
        }

        updateCartBadge()
    }

    private fun openAccount() {
        lastItemSelected = 0
        if (AuthHelper.isLoggedIn()) {
            startActivity(Intent(this, AccountActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun openBudgetsSheet(budgetId: Long?, selectedProductId: Long?) {
        if (AuthHelper.isLoggedIn()) {
            val budgetRequestsFragment = BudgetRequestsFragment()

            val bundle = Bundle()

            if (budgetId != null) {
                bundle.putLong("budget_id", budgetId)
            } else {
                bundle.putLong("budget_id", -1)
            }

            if (selectedProductId != null) {
                bundle.putLong("selected_product_id", selectedProductId)
            } else {
                bundle.putLong("selected_product_id", -1)
            }

            budgetRequestsFragment.arguments = bundle

            budgetRequestsFragment.show(supportFragmentManager, "budgetSheet")
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun isFragmentVisible(fragment: Fragment?): Boolean {
        return fragment != null && fragment.isVisible
    }

    fun updateCartBadge() {
        val cartSize = getCart().size
        if (cartSize == 0) {
            bottom_navigation.setNotification(AHNotification(), 1)
            bottom_navigation.disableItemAtPosition(1)
        } else {
            bottom_navigation.setNotification(getCart().size.toString(), 1)
            bottom_navigation.enableItemAtPosition(1)
        }
    }

}
