package com.avallie.model

import java.io.Serializable

class RequestedProduct(
    var amount: Number,
    var specifications: String,
    val product: Product,
    var budgetsAvaiable: Int?,
    var budgets: ArrayList<Budget>
) : Serializable