package com.poovarasan.tamiltv.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.checkAPI
import kotlinx.coroutines.launch

@Composable
fun ProDialog(isOpen: Boolean = false, onDismiss: () -> Unit) {
    var value by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var context = LocalContext.current
    var scope = rememberCoroutineScope()

    fun onSubmit() {
        if (value.isNotEmpty() || value.isNotBlank()) {

            scope.launch {
                try {
                    var response = context.checkAPI(value)
                    status = "Procode Added"
                    Prefs.putString("procode", value)
                    onDismiss()
                } catch (e: Exception) {
                    status = "Invalid Procode"
                }
            }

        }
    }

    if (isOpen) {

        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = colorResource(R.color.appbarcolor))
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pro Code",
                        style = MaterialTheme.typography.body1.copy(fontSize = 16.sp),
                        modifier = Modifier.padding(bottom = 10.dp),
                        color = colorResource(R.color.textcolor)
                    )


                    BasicTextField(
                        value = value,
                        onValueChange = { x -> value = x },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .background(colorResource(R.color.tfbg))
                                    .clip(RoundedCornerShape(6.dp))
                                    .height(44.dp)
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                            }
                        }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = status, color = Color.Red)
                    }


                    Row {
                        TextButton(onClick = onDismiss) {
                            Text(text = "Cancel")
                        }

                        Box(modifier = Modifier.weight(1f))
                        TextButton(onClick = { onSubmit() }) {
                            Text(text = "Verify & Add")
                        }
                    }

                }
            }
        }
    }
}