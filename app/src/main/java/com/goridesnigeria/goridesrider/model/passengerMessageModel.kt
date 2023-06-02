package com.goridesnigeria.gorides.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goridesnigeria.gorides.activities.Repo.passengerchat

class passengerMessageModel: ViewModel() {

    private val repository: passengerchat
    private val allpassengermessages = MutableLiveData<List<passengerMessageClass>>()
    val _allpassengermessages : MutableLiveData<List<passengerMessageClass>> = allpassengermessages

    init {
        repository = passengerchat().getInstance()
        repository.loadpassengerMessages(_allpassengermessages)
    }
}