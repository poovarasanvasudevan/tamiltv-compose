package com.poovarasan.tamiltv.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray

class VM : ViewModel() {
    private val _channels = MutableLiveData<JsonArray>(JsonArray())
    val channels: LiveData<JsonArray> = _channels


    private val _radio = MutableLiveData<List<Radio>>(emptyList())
    val radio: LiveData<List<Radio>> = _radio


    private val _lastItem = MutableLiveData<Int>(0)
    val lastItem: LiveData<Int> = _lastItem

    fun setChannels(json: JsonArray) {
        _channels.value = json
    }

    fun setRadio(json: List<Radio>) {
        _radio.value = json
    }

    fun setLastItem(item: Int) {
        _lastItem.value = item
    }
}