package com.avallie.model

class SelectedProduct(var quantity: Number, var observation: String, val product: Product) : Product(product.id, product.name, product.category, product.especification, product.unity, product.addToCart)