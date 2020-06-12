package com.avallie.helpers

import com.avallie.model.ConstructionPhase
import com.avallie.model.Customer
import com.avallie.model.RequestedProduct
import com.avallie.model.request.SelectedProduct
import io.paperdb.Paper

class PaperHelper {

    companion object {
        fun savePhases(phases: ArrayList<ConstructionPhase>) {
            Paper.book().write("phases", phases)
        }

        fun getPhases(): ArrayList<ConstructionPhase> {
            return Paper.book().read("phases", ArrayList())
        }

        fun hasPhases(): Boolean {
            return Paper.book().contains("phases")
        }

        fun addProduct(selectedProduct: SelectedProduct) {
            val cart = getCart()
            cart.add(selectedProduct)
            Paper.book().write("cart", cart)
        }

        fun getCart(): ArrayList<SelectedProduct> {
            return Paper.book().read("cart", ArrayList())
        }

        fun updateCart(selectedProducts: ArrayList<SelectedProduct>) {
            Paper.book().write("cart", selectedProducts)
        }

        fun clearCart() {
            Paper.book().delete("cart")
        }

        fun saveCustomer(customer: Customer) {
            Paper.book().write("customer", customer)
        }

        fun getCustomer(): Customer? {
            return if (Paper.book().contains("customer")) {
                Paper.book().read("customer")
            } else {
                null
            }
        }

        fun clearCustomer(){
            Paper.book().delete("customer")
        }
    }

}