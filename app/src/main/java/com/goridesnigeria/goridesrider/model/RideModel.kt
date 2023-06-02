package com.goridesnigeria.gorides.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goridesnigeria.goridesrider.model.rides

class RideModel :ViewModel() {

    private val repository: rides
    private val allrides = MutableLiveData<List<RidesClass>>()
    val _allrides : MutableLiveData<List<RidesClass>> = allrides

    init {
        repository = rides().getInstance()
        repository.loadrides(_allrides)
    }
}
