package com.poovarasan.tamiltv.ui.theme

import androidx.compose.ui.graphics.Color

object ColorUtils {
    fun getColor(colorString: String): Color {
        return Color(android.graphics.Color.parseColor(colorString))
    }
}

val BG_LIGHT = Color(0xffffffff)
val BG_DARK = Color(0xff121212)

val Red200 = Color(0xfff297a2)
val Red300 = Color(0xffea6d7e)
val Red700 = Color(0xffdd0d3c)
val Red800 = Color(0xffd00036)
val Red900 = Color(0xffc20029)

val TColor= arrayListOf<Color>(
    ColorUtils.getColor("#009989"),
    ColorUtils.getColor("#0099ED"),
    ColorUtils.getColor("#FF2F3A"),
    ColorUtils.getColor("#587E8A"),
    ColorUtils.getColor("#A721AB"),
    ColorUtils.getColor("#FD0062"),
    ColorUtils.getColor("#14B15C"),
    ColorUtils.getColor("#00BFD3"),
    ColorUtils.getColor("#7FC35B"),
    ColorUtils.getColor("#0F52F5"),
    ColorUtils.getColor("#009989"),
    ColorUtils.getColor("#FF9130"),
)


