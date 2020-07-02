package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Budget(
    @SerializedName("supplier")
    val supplier: Supplier,
    @SerializedName("payment_methods")
    val paymentOption: String,
    @SerializedName("delivery_specifications")
    var deliveryOption: String,
    @SerializedName("product_status")
    val productStatus: String,
    @SerializedName("value")
    val totalPrice: Double,
    val discountPercentage: Double,
    @SerializedName("shipping_fee")
    val shippingPrice: Double
) : Serializable