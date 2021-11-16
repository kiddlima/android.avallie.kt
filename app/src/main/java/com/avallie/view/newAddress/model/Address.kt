package com.avallie.view.newAddress.model

import com.google.android.libraries.places.api.model.Place
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Address(
    place: Place
) : Serializable {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("place_id")
    var placeId: String? = null

    @SerializedName("street")
    lateinit var street: String

    @SerializedName("street_number")
    var streetNumber: Int? = null

    @SerializedName("additional_address")
    var additionalAddress: String? = null

    @SerializedName("state")
    lateinit var state: String

    @SerializedName("district")
    var district: String? = null

    @SerializedName("country")
    lateinit var country: String

    @SerializedName("city")
    lateinit var city: String

    @SerializedName("postal_code")
    lateinit var postalCode: String

    @SerializedName("latitude")
    var latitude: Double = 0.0

    @SerializedName("longitude")
    var longitude: Double = 0.0

    fun hasNumber(): Boolean {
        return streetNumber != null
    }

    init {
        place.latLng?.run {
            this@Address.latitude = latitude
            this@Address.longitude = longitude
        }

        placeId = place.id

        place.addressComponents?.let {
            for (addressComponent in it.asList()) {
                for (type in addressComponent.types) {
                    when (type) {
                        "country" -> this@Address.country =
                            if (addressComponent.shortName != null) addressComponent.shortName!! else addressComponent.name
                        "administrative_area_level_2" -> this@Address.city =
                            addressComponent.name
                        "route" -> this@Address.street = addressComponent.name
                        "administrative_area_level_1" -> this@Address.state =
                            if (addressComponent.shortName != null) addressComponent.shortName!! else addressComponent.name
                        "postal_code" -> this@Address.postalCode = addressComponent.name
                        "street_number" -> this@Address.streetNumber =
                            addressComponent.name.toInt()
                        "sublocality_level_1" -> this@Address.district =
                            addressComponent.name
                    }
                }
            }
        }
    }

}