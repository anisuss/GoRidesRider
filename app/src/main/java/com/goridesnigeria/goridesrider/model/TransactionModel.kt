package com.goridesnigeria.goridesrider.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class TransactionModel: ViewModel() {

    private val repository: transaction
    private val alltransactions = MutableLiveData<List<TransactionClass>>()
    val _alltransactions : MutableLiveData<List<TransactionClass>> = alltransactions

    init {
        repository = transaction().getInstance()
        repository.loadtransaction(_alltransactions)
    }
}