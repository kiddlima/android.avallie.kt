package com.avallie.model.request

import com.avallie.model.Product
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SelectedProduct(
    @SerializedName("amount")
    var amount: Number,
    @SerializedName("specifications")
    var specifications: String,
    @SerializedName("product_id")
    val productId: Long,
    val brand: String?,
    val product: Product
) : Serializable