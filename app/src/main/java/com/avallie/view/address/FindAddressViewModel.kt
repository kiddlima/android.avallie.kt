package com.avallie.view.address

import android.content.Context
import android.gesture.Prediction
import androidx.lifecycle.MutableLiveData
import com.avallie.core.CustomViewModel
import com.avallie.model.ScreenState
import com.avallie.view.address.model.Address
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse

class FindAddressViewModel : CustomViewModel() {

    private val token = AutocompleteSessionToken.newInstance()

    var predictions = MutableLiveData<MutableList<AutocompletePrediction>>()

    interface OnPlaceSearch {
        fun onFind(address: Address)
    }

    interface OnPredictionSearch {
        fun onFind(prediction: AutocompletePrediction)
    }

    fun searchPlaces(
        query: String,
        context: Context,
        onPredictionSearch: OnPredictionSearch? = null
    ) {
        mState.value = ScreenState.Loading

        val placesClient = Places.createClient(context)

        predictions.value = mutableListOf()

        val request =
            FindAutocompletePredictionsRequest.builder()
                .setCountries("BR")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                this.predictions.value!!.addAll(response.autocompletePredictions)

                mState.value = ScreenState.Success

                onPredictionSearch?.onFind(this.predictions.value!![0])

            }.addOnFailureListener {
                mState.value = ScreenState.Fail
            }
    }

    fun findPlace(
        placeId: String,
        context: Context,
        onPlaceSearch: OnPlaceSearch
    ) {
        mState.value = ScreenState.Loading

        val placesClient = Places.createClient(context)

        val placeFields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS
        )

        val request =
            FetchPlaceRequest.builder(placeId, placeFields).setSessionToken(token).build()
        placesClient.fetchPlace(request).addOnSuccessListener { response ->

            val place: Place = response.place

            mState.value = ScreenState.Success

            onPlaceSearch.onFind(Address(place))

        }.addOnFailureListener { exception ->
            if (exception is ApiException) {

            }
        }
    }

}