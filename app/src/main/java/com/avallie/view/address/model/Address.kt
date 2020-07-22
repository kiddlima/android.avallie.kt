package com.avallie.view.address.model

import com.google.android.libraries.places.api.model.Place
import java.io.Serializable

class Address(
    place: Place
) : Serializable {

    init {
        place.latLng?.run {
            this@Address.latitude = latitude
            this@Address.longitude = longitude
        }

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
                            addressComponent.name
                        "sublocality_level_1" -> this@Address.district =
                            addressComponent.name
                    }
                }
            }
        }
    }

    lateinit var street: String
    var streetNumber: String? = null
    var additionalAddress: String? = null
    lateinit var state: String
    lateinit var district: String
    lateinit var country: String
    lateinit var city: String
    lateinit var postalCode: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun hasNumber(): Boolean {
        return streetNumber != null
    }

}