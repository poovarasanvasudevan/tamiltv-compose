package com.poovarasan.tamiltv.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.ui.theme.TColor


@Composable
fun DrawerRow(
    icon: ImageVector = Icons.Rounded.Category,
    title: String,
    selected: Boolean,
    iconColor: Color = TColor[0],
    onClick: () -> Unit,

    ) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp, 10.dp, 16.dp, 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(
                        color = iconColor,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .padding(3.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = "Language Icon",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.body1.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W400
                ),
                color = colorResource(R.color.textcolor)
            )
        }
    }
}