package com.avallie.model

import java.io.Serializable

class RequestedProduct(
    var amount: Number,
    var specifications: String,
    val product: Product,
    var budgetsAvaiable: Int?,
    var budgets: ArrayList<Budget>
) : Product(product.id, product.name, product.category, product.specification, product.unit, product.addToCart),
    Serializable