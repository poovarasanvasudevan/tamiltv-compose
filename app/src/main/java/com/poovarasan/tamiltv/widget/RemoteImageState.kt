package com.poovarasan.tamiltv.widget

import android.graphics.Bitmap

sealed class RemoteImageState {

    object Loading : RemoteImageState()

    data class Loaded(
        val image: Bitmap
    ) : RemoteImageState()

    object LoadError : RemoteImageState()

}