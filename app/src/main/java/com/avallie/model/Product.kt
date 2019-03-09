package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Product(
        @SerializedName("_id")
        var id: String,
        var name: String,
        var category: String,
        var especification: String?,
        var unity: String,
        var addToCart: Boolean?

) : Serializable