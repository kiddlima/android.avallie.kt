package com.avallie.model

import java.io.Serializable

class RequestedProduct(
    var id: Long,
    var amount: Number,
    var specifications: String,
    val product: Product,
    var budgetsAvaiable: Int?,
    var budgets: MutableList<Budget>
) : Serializable