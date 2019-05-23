package com.avallie.model

import java.io.Serializable

class Budget(val supplierName: String,
             val paymentOption: String,
             var deliveryOption: String,
             val productStatus: String,
             val totalPrice: Double,
             val finalPrice: Double,
             val discountPercentage: Double,
             val shippingPrice: Double): Serializable