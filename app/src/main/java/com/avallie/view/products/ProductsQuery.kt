package com.avallie.view.products

class ProductsQuery(
    var page: Int = 0,
    val size: Int = 20,
    var categories: MutableList<String>,
    var name: String
)