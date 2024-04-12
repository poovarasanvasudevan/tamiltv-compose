package com.poovarasan.tamiltv.pages

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.AudioVM
import com.poovarasan.tamiltv.core.Radio
import com.poovarasan.tamiltv.core.VM
import com.poovarasan.tamiltv.core.getRadio
import com.poovarasan.tamiltv.core.isOnline
import com.poovarasan.tamiltv.core.showIntAd
import com.poovarasan.tamiltv.widget.AppBanner
import com.poovarasan.tamiltv.widget.ChannelItem
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RadioChannels(
    navController: NavController,
    vm: VM = viewModel(),
    audioVM: AudioVM = viewModel()
) {

    val context = LocalContext.current as Activity
    val channels by vm.radio.observeAsState()
    val scope = rememberCoroutineScope()
    var isRefresh by remember { mutableStateOf(false) }
    val systemUI = rememberSystemUiController()

    LaunchedEffect(Unit) {
        context.showIntAd {
        }
    }

    fun loadData() {
        if (context.isOnline()) {
            isRefresh = true
            scope.launch {
                val radio = context.getRadio()
                vm.setRadio(radio.get("radio").asJsonArray.map { x ->
                    Radio(
                        url = x.asJsonObject.get("url").asString,
                        logo = x.asJsonObject.get("logo").asString,
                        name = x.asJsonObject.get("name").asString,
                    )
                })
                isRefresh = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colorResource(R.color.appbarcolor),
                title = { Text("Radio", color = colorResource(R.color.textcolor)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Rounded.ArrowBack, "", tint = colorResource(R.color.textcolor))
                    }
                }
            )
        }
    ) {

        systemUI.setStatusBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())
        systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())


        if (context.isOnline()) {
            Column(modifier = Modifier.fillMaxSize().background(colorResource(R.color.bgcolor))) {

                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefresh),
                    onRefresh = { loadData() },
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn {
                        items(items = channels!!) { item ->
                            ChannelItem(
                                imageUrl = item.logo ?: "",
                                channelName = item.name ?: "",
                                channelCategory = "Radio"
                            ) {

                                audioVM.setCurrent(fm = listOf(item))

                            }
                        }
                    }
                }


                AppBanner(
                    modifier = Modifier.fillMaxWidth()
                )

            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(colorResource(R.color.bgcolor)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.inet), "no internet")
            }
        }
    }
}