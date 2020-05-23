package com.avallie.model

import java.io.Serializable

open class Product(
    var id: Long,
    var name: String,
    var category: String,
    var specification: String?,
    var unit: String,
    var addToCart: Boolean?

) : Serializable