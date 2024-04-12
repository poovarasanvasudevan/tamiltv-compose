package com.poovarasan.tamiltv.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object ThemeState {
    var darkModeState : MutableState<Boolean> = mutableStateOf(false)
}