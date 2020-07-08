package com.avallie.view.products

import com.avallie.model.Product
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductsContainerResponse(
    @SerializedName("content")
    var content: MutableList<Product>,
    @SerializedName("pageable")
    var pageable: Pageable,
    @SerializedName("totalElements")
    var totalElements: Int,
    @SerializedName("totalPages")
    var totalPages: Int,
    var last: Boolean
) : Serializable {
    inner class Pageable (
        @SerializedName("pageSize")
        var pageSize: Int,
        @SerializedName("pageNumber")
        var pageNumber: Int
    ): Serializable
}