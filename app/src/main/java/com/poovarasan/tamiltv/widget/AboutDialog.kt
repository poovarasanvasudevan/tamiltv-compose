package com.poovarasan.tamiltv.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.poovarasan.tamiltv.BuildConfig
import com.poovarasan.tamiltv.R


@Composable
fun AboutDialog(isOpen: Boolean = false, onClose: () -> Unit = {}, onPolicy: () -> Unit = {}) {

    if (isOpen) {
        Dialog(onDismissRequest = { onClose() }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
                    .background(color = Color.White)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(modifier = Modifier.height(10.dp))
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher_tv),
                        contentDescription = "icon",
                        modifier = Modifier
                            .width(60.dp)
                            .requiredHeight(60.dp)
                            .height(60.dp)
                    )

                    Box(modifier = Modifier.height(24.dp))
                    Text(
                        "Tamil TV",
                        style = MaterialTheme.typography.body1.copy(fontSize = 18.sp)
                    )
                    Text(
                        "Version: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.caption
                    )
                    Box(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {  }) {
                            Text("Play Store")
                        }
                        Box(modifier = Modifier.width(24.dp))
                        TextButton(onClick = { onPolicy() }) {
                            Text("Privacy Policy")
                        }
                    }
                }
            }
        }

    }
}