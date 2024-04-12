package com.poovarasan.tamiltv.pages

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.AppChannel
import com.poovarasan.tamiltv.core.Route
import com.poovarasan.tamiltv.core.checkAPI
import com.poovarasan.tamiltv.core.showIntAd
import com.poovarasan.tamiltv.widget.AppBanner
import com.poovarasan.tamiltv.widget.BackHandler
import com.poovarasan.tamiltv.widget.ChannelItem
import com.poovarasan.tamiltv.widget.ProDialog
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProChannels(navController: NavController) {

    var dialog by remember { mutableStateOf(false) }
    val context = LocalContext.current as Activity
    var prochannel by remember { mutableStateOf(emptyList<JsonObject>()) }

    val scope = rememberCoroutineScope()

    val systemUI = rememberSystemUiController()

    fun loadChannel() {
        if (Prefs.contains("procode")) {
            scope.launch {
                try {
                    val response = context.checkAPI(Prefs.getString("procode"))
                    prochannel = response.map { it.asJsonObject }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun back() {
        navController.navigateUp()
    }
    BackHandler {
        back()
    }
    LaunchedEffect(Unit) {
        context.showIntAd {

        }
        loadChannel()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorResource(R.color.appbarcolor),
                title = { Text("Other Channels", color = colorResource(R.color.textcolor)) },
                actions = {
                    IconButton(onClick = { dialog = !dialog }) {
                        Icon(Icons.Rounded.Add, "", tint = colorResource(R.color.textcolor))
                    }
                },

                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(Icons.Rounded.ArrowBack, "", tint = colorResource(R.color.textcolor))
                    }
                }
            )
        },
    ) {

        systemUI.setStatusBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())
        systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())


        ProDialog(isOpen = dialog) {
            dialog = false
            loadChannel()
        }

        if (prochannel.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nodata),
                    contentDescription = "nodata",
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .padding(12.dp)
                )

                Text(
                    text = "No Records Found",
                    style = MaterialTheme.typography.body1,
                    color = colorResource(R.color.textcolor)
                )
            }
        } else {
            Column {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(items = prochannel) { index, i ->
                        ChannelItem(
                            imageUrl = i.get("logo").asString,
                            channelName = i.get("name").asString,
                            channelCategory = i.get("category").asString
                        ) {
                            context.showIntAd {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "channel",
                                    value =  AppChannel(
                                        channelId = index.toLong(),
                                        category = i.get("category").asString,
                                        url = i.get("url").asString,
                                        name = i.get("name").asString,
                                        logo = i.get("logo").asString
                                    )
                                )
                                navController.navigate(Route.Player) {
                                    popUpTo(Route.Premium) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
                AppBanner(
                    modifier = Modifier.fillMaxWidth()
                )

            }

        }
    }
}