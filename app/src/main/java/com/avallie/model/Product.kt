package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Product : Serializable {

    @SerializedName("_id")
    var id: String? = null
    var name: String? = null
    var category: String? = null
    var especification: String? = null
    var unity: String? = null
    var addToCart: Boolean = false

}