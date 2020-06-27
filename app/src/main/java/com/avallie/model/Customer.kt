package com.avallie.model

import com.google.gson.annotations.SerializedName

class Customer {
    var cpf: String? = null
    var email: String? = null
    var name: String? = null
    var password: String? = null
    var telephone: String? = null
    var city: String? = null
    var state: String? = null
    var street: String? = null
    @SerializedName("street_number")
    var streetNumber: String? = null
    @SerializedName("zip_code")
    var zipCode: String? = null
    @SerializedName("company_name")
    var companyName: String? = null
    @SerializedName("additional_address")
    var additionalAddress: String? = null
}