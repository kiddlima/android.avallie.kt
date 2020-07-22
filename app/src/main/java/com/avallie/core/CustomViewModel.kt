package com.avallie.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avallie.model.ScreenState

open class CustomViewModel : ViewModel() {

    val mState = MutableLiveData<ScreenState>()

}