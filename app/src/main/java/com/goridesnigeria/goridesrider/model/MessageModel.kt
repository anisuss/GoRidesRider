package com.goridesnigeria.gorides.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goridesnigeria.gorides.activities.Repo.messages

class MessageModel: ViewModel() {

    private val repository: messages
    private val allmessages = MutableLiveData<List<MesssageClass>>()
    val _allmessages : MutableLiveData<List<MesssageClass>> = allmessages

    init {
        repository = messages().getInstance()
        repository.loadmessages(_allmessages)
    }
}