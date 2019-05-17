package com.avallie.model

class SelectedProduct(var quantity: Number, var observation: String, val product: Product, var budgetsAvaiable: Int?) : Product(product.id, product.name, product.category, product.especification, product.unity, product.addToCart)