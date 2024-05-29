package com.poovarasan.tamiltv

object Ads {
    val EMAIL = "poovarasan.dev@gmail.com"
    val SUBJECT = "Tamil TV Help / Feedback"
}

class ConfigHelper {
    companion object {
       init {
           System.loadLibrary("native-lib")
       }

        @JvmStatic
        external fun apiUrl(): String

        @JvmStatic
        external fun getMUrl(): String

        @JvmStatic
        external fun getRUrl(): String

        @JvmStatic
        external fun getUserAgent(): String

        @JvmStatic
        external fun getSettings(): String
    }


}