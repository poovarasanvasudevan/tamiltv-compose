package com.poovarasan.tamiltv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.poovarasan.tamiltv.core.Route
import com.poovarasan.tamiltv.core.initAd
import com.poovarasan.tamiltv.pages.AppPlayer
import com.poovarasan.tamiltv.pages.CategoryChannel
import com.poovarasan.tamiltv.pages.FavouriteChannel
import com.poovarasan.tamiltv.pages.Home
import com.poovarasan.tamiltv.pages.ProChannels
import com.poovarasan.tamiltv.pages.RadioChannels
import com.poovarasan.tamiltv.pages.SplashPage
import com.poovarasan.tamiltv.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }

        initAd { }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}

@Composable
fun MainView() {
    val navController = rememberNavController()
    val systemUI = rememberSystemUiController()

    AppTheme {
        systemUI.setStatusBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())
        systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())

        Surface(color = colorResource(R.color.bgcolor) , modifier = Modifier.statusBarsPadding().navigationBarsPadding()) {

            NavHost(navController = navController, startDestination = Route.Splash) {
                composable(Route.Splash) { SplashPage(navController = navController) }
                composable(Route.Home) { Home(navController = navController) }
                composable(Route.Player) { AppPlayer(navController = navController) }
                composable(Route.Premium) { ProChannels(navController = navController) }
                composable(Route.Category) { CategoryChannel(navController = navController) }
                composable(Route.Radio) { RadioChannels(navController = navController) }
                composable(Route.Favourite) { FavouriteChannel(navController = navController) }
            }
        }
    }
}
