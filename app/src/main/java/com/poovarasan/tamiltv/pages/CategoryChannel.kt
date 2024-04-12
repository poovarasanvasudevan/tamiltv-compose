package com.poovarasan.tamiltv.pages

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.AppChannel
import com.poovarasan.tamiltv.core.Route
import com.poovarasan.tamiltv.core.TamilTV
import com.poovarasan.tamiltv.core.showIntAd
import com.poovarasan.tamiltv.widget.AppBanner
import com.poovarasan.tamiltv.widget.BackHandler
import com.poovarasan.tamiltv.widget.ChannelItem
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList

@SuppressLint("FlowOperatorInvokedInComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CategoryChannel(navController: NavController) {
    val categoryString = navController.previousBackStackEntry?.savedStateHandle?.get<String>("category")
    val systemUI = rememberSystemUiController()

    val channelQuery = TamilTV.database.channelsQueries
    val context = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        context.showIntAd {

        }
    }


    fun back() {
        navController.navigateUp()
    }
    BackHandler {
        back()
    }

    if (categoryString != null) {

        val channels by channelQuery.getCategoryChannels(categoryString).asFlow()
            .mapToList()
            .collectAsState(initial = emptyList())


        Scaffold (
            topBar = {
                TopAppBar (
                    backgroundColor = colorResource(R.color.appbarcolor),
                    title = { Text(categoryString, color = colorResource(R.color.textcolor)) },
                    navigationIcon = {
                        IconButton(onClick = { back() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                tint = colorResource(R.color.textcolor),
                                contentDescription = "back"
                            )
                        }
                    }
                )
            }
        ) {

            systemUI.setStatusBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())
            systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())


            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {

                    items(items = channels) { channel ->
                        ChannelItem(
                            imageUrl = channel.channelLogo,
                            channelName = channel.channelName,
                            channelCategory = channel.channelCategory
                        ) {

                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                key = "channel",
                                value = AppChannel(
                                    channelId = channel.channelId,
                                    category = channel.channelCategory,
                                    url = channel.channelStream,
                                    name = channel.channelName,
                                    logo = channel.channelLogo,
                                )
                            )
                            navController.navigate(Route.Player) {
                                popUpTo(Route.Category) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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