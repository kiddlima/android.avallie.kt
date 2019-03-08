package com.avallie.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avallie.view.fragment.ProductsFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class MainActivity : AppCompatActivity() {

    private var categories: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.avallie.R.layout.activity_main)

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
}
