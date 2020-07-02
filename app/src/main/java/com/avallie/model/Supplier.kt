package com.avallie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Supplier(
    @SerializedName("name")
    val name: String,
    @SerializedName("phone_number")
    val phone: String,
    @SerializedName("email")
    val email: String
) : Serializable