package com.avallie.model

import java.io.Serializable

class SelectedProduct(var quantity: Number,
                      var observation: String,
                      val product: Product,
                      var budgetsAvaiable: Int?,
                      var budgets: ArrayList<Budget>) : Product(product.id, product.name, product.category, product.specification, product.unit, product.addToCart), Serializable