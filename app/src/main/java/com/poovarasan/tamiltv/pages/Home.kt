package com.poovarasan.tamiltv.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.MarkChatRead
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.BuildConfig
import com.poovarasan.tamiltv.Channels
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.AppChannel
import com.poovarasan.tamiltv.core.AudioVM
import com.poovarasan.tamiltv.core.Radio
import com.poovarasan.tamiltv.core.Route
import com.poovarasan.tamiltv.core.TamilTV
import com.poovarasan.tamiltv.core.ThemeState
import com.poovarasan.tamiltv.core.VM
import com.poovarasan.tamiltv.core.getRadio
import com.poovarasan.tamiltv.core.help
import com.poovarasan.tamiltv.core.isOnline
import com.poovarasan.tamiltv.core.showIntAd
import com.poovarasan.tamiltv.ui.theme.TColor
import com.poovarasan.tamiltv.widget.AppBanner
import com.poovarasan.tamiltv.widget.BackHandler
import com.poovarasan.tamiltv.widget.ChannelItem
import com.poovarasan.tamiltv.widget.DrawerRow
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.startapp.sdk.adsbase.StartAppAd
import kotlinx.coroutines.launch


@SuppressLint("FlowOperatorInvokedInComposition", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Home(
    navController: NavController,
    vm: VM = viewModel(),
    audioVM: AudioVM = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val systemUI = rememberSystemUiController()
    val listState = rememberLazyListState()
    val isDarkMode = ThemeState.darkModeState.value

    val channelQuery = TamilTV.database.channelsQueries

    val context = LocalContext.current as Activity
    val listIndex by vm.lastItem.observeAsState(0)

    var backRequest by remember { mutableStateOf(false) }

    val radios by vm.radio.observeAsState()

    val liveChannels by channelQuery.getAllChannels()
        .asFlow()
        .mapToList()
        .collectAsState(
            initial = emptyList()
        )

    val categories by channelQuery.getAllCategories().asFlow().mapToList()
        .collectAsState(initial = emptyList())

    var value by remember { mutableStateOf("") }


    var filteredChannels by remember { mutableStateOf(emptyList<Channels>()) }

    fun loadData() {
        if (context.isOnline()) {
            scope.launch {
                val radio = context.getRadio()
                vm.setRadio(radio.get("radio").asJsonArray.map { x ->
                    Radio(
                        url = x.asJsonObject.get("url").asString,
                        logo = x.asJsonObject.get("logo").asString,
                        name = x.asJsonObject.get("name").asString,
                    )
                })
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Prefs.getInt("mode") > 0) {
            scope.launch {
                listState.scrollToItem(listIndex)
            }
        } else {
            loadData()
        }
    }


    BackHandler {
        if (scaffoldState.drawerState.isOpen) {
            scope.launch { scaffoldState.drawerState.close() }
        } else {
            backRequest = true
        }
    }



    fun filterChannelsWithStr(filter: String) {
        value = filter
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var focusTargetItem by remember { mutableStateOf(0) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(

                title = {
                    Text("Radio & TV", color = colorResource(R.color.textcolor))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }, modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Menu,
                            contentDescription = "Menu",
                            tint = colorResource(R.color.textcolor)
                        )
                    }
                },
                backgroundColor = colorResource(R.color.appbarcolor)

            )
        },

        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(R.color.appbarcolor))
            ) {
                Column(
                    modifier = Modifier
                        .focusable()
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        stringResource(id = R.string.short_app_name),
                        style = MaterialTheme.typography.body1.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colorResource(R.color.textcolor)
                    )
                    Box(modifier = Modifier.height(5.dp))
                    Text(
                        "Version: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.caption,
                        color = colorResource(R.color.textcolor)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .focusable()
                        .weight(1f)
                ) {
                    if (Prefs.getInt("mode") > 0) {
                        itemsIndexed(items = categories) { index, category ->
                            DrawerRow(
                                title = category,
                                selected = false,
                                iconColor = TColor[index % TColor.size]
                            ) {
                                navController.currentBackStackEntry?.arguments.apply {
                                    this?.putString("category", category)
                                }

                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = "category",
                                    value = category
                                )

                                vm.setLastItem(listState.firstVisibleItemIndex)
                                navController.navigate(Route.Category) {
                                    popUpTo(Route.Home) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(color = colorResource(R.color.adivider))
                        )
                        Box(modifier = Modifier.height(4.dp))
                    }

                    item {
                        DrawerRow(
                            icon = Icons.Rounded.QueueMusic,
                            title = "Radio",
                            selected = false,
                            iconColor = TColor[5]
                        ) {
                            navController.navigate(Route.Radio)
                        }
                        DrawerRow(
                            icon = Icons.Rounded.StarBorder,
                            title = "Favourites",
                            selected = false,
                            iconColor = TColor[6]
                        ) { navController.navigate(Route.Favourite) }

                        DrawerRow(
                            icon = Icons.Rounded.Apps,
                            title = "Other",
                            selected = false,
                            iconColor = TColor[4]
                        ) {
                            navController.navigate(Route.Premium)
                        }
                    }

                    item {
                        Box(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(color = colorResource(R.color.adivider))
                        )
                        Box(modifier = Modifier.height(4.dp))
                    }

                    item {

                        DrawerRow(
                            icon = Icons.Rounded.Notes,
                            title = "FAQ",
                            selected = false,
                            iconColor = TColor[6]
                        ) {
                            val uri: Uri =
                                Uri.parse("https://tvchannels.inditechman.com/indiatvfaq.html")
                            val goMarket = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(goMarket)
                        }
                        DrawerRow(
                            icon = Icons.Rounded.StarBorder,
                            title = "Rate us",
                            selected = false,
                            iconColor = TColor[3]
                        ) {
                            try {
                                val uri: Uri =
                                    Uri.parse("market://details?id=${context.packageName}")
                                val goMarket = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(goMarket)
                            } catch (e: ActivityNotFoundException) {
                                val uri: Uri =
                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                val goMarket = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(goMarket)
                            }
                        }

                        DrawerRow(
                            icon = Icons.Rounded.MarkChatRead,
                            title = "Feedback / Issue",
                            selected = false,
                            iconColor = TColor[4]
                        ) { context.help() }

                        Box(modifier = Modifier.height(40.dp))
                    }

                }

            }
        },
        drawerShape = RoundedCornerShape(0.dp)
    ) {

//
        systemUI.setStatusBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())
        systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())

        if (backRequest) {
            AlertDialog(
                onDismissRequest = { backRequest = false },
                text = {
                    Text(
                        "Are you sure want to exit?",
                        style = MaterialTheme.typography.body1,
                        color = colorResource(R.color.textcolor)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        backRequest = false
                        StartAppAd.onBackPressed(context);
                        context.finish()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { backRequest = false }) {
                        Text("No")
                    }
                },
            )
        }
        //UpdateDialog()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.bgcolor))
        ) {

            LazyColumn(
                modifier = Modifier
                    .focusTarget()
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                flingBehavior = ScrollableDefaults.flingBehavior(),
            ) {
                if (Prefs.getInt("mode") > 0) {
                    itemsIndexed(items = if (value.length >= 3) filteredChannels else liveChannels) { index, c ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .focusable()
                        ) {
                            ChannelItem(
                                imageUrl = c.channelLogo,
                                channelName = c.channelName,
                                channelCategory = c.channelCategory
                            ) {
                                context.showIntAd {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        key = "channel",
                                        value = AppChannel(
                                            channelId = c.channelId,
                                            category = c.channelCategory,
                                            url = c.channelStream,
                                            name = c.channelName,
                                            logo = c.channelLogo,
                                        )
                                    )

                                    vm.setLastItem(listState.firstVisibleItemIndex)
                                    navController.navigate(Route.Player)
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(items = radios!!) { index, item ->
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
            }

            AppBanner(
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
