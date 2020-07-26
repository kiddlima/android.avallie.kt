package com.avallie.model

import com.avallie.view.address.model.Address
import com.google.gson.annotations.SerializedName

class Customer {
    var cpf: String? = null
    var email: String? = null
    var name: String? = null
    var password: String? = null
    var telephone: String? = null
    var addresses: MutableList<Address>? = null

    @SerializedName("company_name")
    var companyName: String? = null
}