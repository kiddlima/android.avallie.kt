package com.avallie.model.request

import com.google.gson.annotations.SerializedName

class BudgetRequest(
    @SerializedName("name")
    val budgetName: String,
    @SerializedName("dead_line")
    val budgetDate: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("selected_products")
    var products: MutableList<SelectedProduct>?
)