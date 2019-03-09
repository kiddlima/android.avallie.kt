package com.avallie.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.aurelhubert.ahbottomnavigation.notification.AHNotification
import com.avallie.R
import com.avallie.helpers.PaperHelper.Companion.getCart
import com.avallie.view.fragment.ProductsFragment
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class MainActivity : AppCompatActivity() {

    private var categories: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.avallie.R.layout.activity_main)

        createMenu()

        goToProducts()
    }

    private fun goToProducts() {
        val bundle = Bundle()
        val fragment = ProductsFragment()

        bundle.putStringArrayList("categories", categories)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(com.avallie.R.id.container, fragment).commit()
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

            currentItem = 0
        }

        updateCartBadge()
    }

    fun updateCartBadge() {
        val cartSize = getCart().size
        if (cartSize == 0) {
            bottom_navigation.setNotification(AHNotification(), 1)
        } else {
            bottom_navigation.setNotification(getCart().size.toString(), 1)
        }
    }
}
