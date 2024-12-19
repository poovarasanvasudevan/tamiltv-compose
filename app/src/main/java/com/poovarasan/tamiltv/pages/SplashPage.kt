package com.poovarasan.tamiltv.pages

//import com.poovarasan.tamiltv.core.isOnline
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.Route
import com.poovarasan.tamiltv.core.TamilTV
import com.poovarasan.tamiltv.core.getCoder
import com.poovarasan.tamiltv.core.getConfig
import com.poovarasan.tamiltv.core.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SplashPage(navController: NavController) {

    val channelQuery = TamilTV.database.channelsQueries
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isOnline = context.isOnline()

    LaunchedEffect(Unit) {

        if (isOnline) {
            scope.launch(Dispatchers.IO) {
                val allChannels = context.getCoder()

                val datas = mutableListOf<JsonObject>()
                val allCategory = allChannels.map { it.asJsonObject }
                allCategory.forEach {
                    val channels = it.get("channels").asJsonArray
                    channels.forEach { datas.add(it.asJsonObject) }
                }

                channelQuery.deleteAllChannel()
                datas.forEachIndexed { index, ichannel ->
                    channelQuery.addChannel(
                        channelId = if (!ichannel.asJsonObject.has("channelid")) index.toLong() else ichannel.asJsonObject.get("channelid").asLong,
                        channelName = ichannel.asJsonObject.get("name").asString,
                        channelCategory = if (ichannel.asJsonObject.get("category").isJsonNull) "Other" else ichannel.asJsonObject.get("category").asString,
                        channelLogo = if (ichannel.asJsonObject.get("logo").isJsonNull) "" else ichannel.asJsonObject.get("logo").asString,
                        channelStream = ichannel.asJsonObject.get("url").asString,
                        isActive = if (!ichannel.asJsonObject.has("active")) 1 else ichannel.asJsonObject.get("active").asLong
                    )
                }


                val settings = context.getConfig()
                settings.entrySet().forEach {
                    if (it.value.asJsonPrimitive.isBoolean) {
                        Prefs.putBoolean(it.key, it.value.asBoolean)
                    }
                    if (it.value.asJsonPrimitive.isNumber) {
                        Prefs.putInt(it.key, it.value.asInt)
                    }
                    if (it.value.asJsonPrimitive.isString) {
                        Prefs.putString(it.key, it.value.asString)
                    }
                }
            }

            navController.navigate(Route.Home)
        }
    }


    Scaffold {

        if (isOnline) {
            Box(modifier = Modifier.fillMaxSize().background(colorResource(R.color.bgcolor)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.inet), "no internet")
            }
        }
    }
}