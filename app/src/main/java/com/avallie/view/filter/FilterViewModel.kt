package com.avallie.view.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.model.ScreenState

class FilterViewModel : ViewModel() {

    var errorMessage = MutableLiveData<String>()
    var screenState = MutableLiveData<ScreenState>()

}