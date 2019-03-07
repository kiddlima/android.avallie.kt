package com.avallie.model

import com.google.gson.annotations.SerializedName

class Product {

    @SerializedName("_id")
    var id: String? = null
    var name: String? = null
    var category: String? = null
    var especification: String? = null
    var unity: String? = null
    var addToCart: Boolean = false

}