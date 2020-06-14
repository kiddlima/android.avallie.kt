package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BudgetRequested(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val budgetName: String,
    @SerializedName("dead_line")
    val budgetDate: String,
    @SerializedName("address")
    val address: String,
    val budgetsAvailable: Int,
    @SerializedName("selected_products")
    var products: MutableList<RequestedProduct>?
) : Serializable