package com.poovarasan.tamiltv.widget

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import com.poovarasan.tamiltv.BuildConfig
import com.poovarasan.tamiltv.core.UpdateModel
import com.poovarasan.tamiltv.core.openPlayStore

@Composable
fun UpdateDialog() {
    var isOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var mmodel by remember {
        mutableStateOf(UpdateModel())
    }
    LaunchedEffect(Unit) {
        Ion.with(context)
            .load("https://tavapi.inditechman.com/api/update/tamiltv.json")
            .noCache()
            .`as`(object : TypeToken<UpdateModel>() {})
            .setCallback { e, result ->
                if (e == null) {
                    mmodel = result

                    if (result.version > BuildConfig.VERSION_CODE) {
                        isOpen = true
                    }
                }
            }
    }



    if (isOpen) {
        AlertDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            onDismissRequest = { isOpen = false },
            title = {
                Text(mmodel.title ?: "", style = MaterialTheme.typography.body1.copy(fontSize = 18.sp))
            },
            text = {
                Text(
                    mmodel.body ?: "",
                    style = MaterialTheme.typography.body1
                )
            },
            confirmButton = {
                TextButton(onClick = { context.openPlayStore() }) {
                    Text(mmodel.updateButtonText ?: "")
                }
            }
        )
    }
}