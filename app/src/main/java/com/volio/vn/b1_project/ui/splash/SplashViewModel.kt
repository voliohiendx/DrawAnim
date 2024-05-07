package com.volio.vn.b1_project.ui.splash

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.volio.vn.b1_project.base.BaseViewModel
import com.volio.vn.data.usecases.BitcoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val bitcoinUseCase: BitcoinUseCase
) : BaseViewModel() {
    val loading = MutableLiveData(false)

    fun test() = viewModelScope.launch {
        loading.value = true
        val prices = withContext(Dispatchers.IO) { bitcoinUseCase.getBitcoinPrices() }
        loading.value = false

        Log.e("CharacterViewModel", "fetched: $prices")
    }


    fun test2() = viewModelScope.launch {
        loading.value = true
        val prices = withContext(Dispatchers.IO) {
            bitcoinUseCase.getBitcoinPricesByResult()
        }

        if (prices.isSuccess) {
            Log.e("CharacterViewModel", "fetched: $prices")
        } else {
            Log.e("CharacterViewModel", "fetched: ${prices.exceptionOrNull()}")
        }

        loading.value = false


    }
}