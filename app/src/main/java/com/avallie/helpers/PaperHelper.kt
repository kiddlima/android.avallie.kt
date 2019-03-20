package com.avallie.helpers

import com.avallie.model.ConstructionPhase
import com.avallie.model.SelectedProduct
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

        fun updateCart(selectedProducts: ArrayList<SelectedProduct>){
            Paper.book().write("cart", selectedProducts)
        }
    }

}