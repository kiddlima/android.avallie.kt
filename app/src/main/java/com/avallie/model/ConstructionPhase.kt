package com.avallie.model

class ConstructionPhase {

    var id: String? = null
    var name: String? = null
    var categories: ArrayList<String> = ArrayList()
    var categoriesObject: ArrayList<Category> = ArrayList()
    var selected: Boolean = false
    var icon: String? = null

    fun parseCategories() {
        for (category in categories) {
            categoriesObject.add(Category(category, false))
        }
    }
}