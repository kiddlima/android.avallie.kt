package com.avallie.model

import com.google.gson.annotations.SerializedName

class BudgetRequested(
    @SerializedName("name")
    val budgetName: String,
    @SerializedName("dead_line")
    val budgetDate: String,
    @SerializedName("address")
    val address: String,
    val budgetsAvailable: Int,
    @SerializedName("selected_products")
    var products: MutableList<RequestedProduct>?
)