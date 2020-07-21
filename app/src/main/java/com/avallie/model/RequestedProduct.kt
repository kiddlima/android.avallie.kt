package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestedProduct(
    var id: Long,
    var amount: Number,
    var specifications: String,
    var brand: String?,
    @SerializedName("extra_information")
    var extraInformation: String?,
    val product: Product,
    var budgetsAvaiable: Int?,
    var budgets: MutableList<Budget>
) : Serializable