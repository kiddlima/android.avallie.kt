package com.avallie.model

class Category(name: String, selected: Boolean) {

    var name: String? = null
    var selected: Boolean = false

    init {
        this.name = name
        this.selected = selected
    }
}