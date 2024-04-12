package com.poovarasan.tamiltv.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poovarasan.tamiltv.R
import java.util.*

@Composable
fun ChannelItem(
    imageUrl: String,
    isPlaying: Boolean = false,
    channelName: String,
    channelCategory: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(60.dp)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                UrlImage(
                    url = imageUrl,
                    modifier = Modifier.fillMaxSize().padding(3.dp),
                    contentScale = ContentScale.Fit,
                    error = {
                        Image(
                            bitmap = ImageBitmap.imageResource(com.poovarasan.tamiltv.R.drawable.placeholder),
                            ""
                        )
                    },
                    loading = {
                        Image(
                            bitmap = ImageBitmap.imageResource(com.poovarasan.tamiltv.R.drawable.placeholder),
                            ""
                        )
                    },
                )
            }
            Box(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = channelName.trim(),
                    color = colorResource(R.color.textcolor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                    )
                )
                Text(
                    text = channelCategory.trim().lowercase(Locale.ROOT)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    color = colorResource(R.color.textcolor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1.copy(fontSize = 14.sp)
                )
            }

            if(isPlaying) {
                Icon(Icons.Rounded.PlayArrow, "")
            }
        }
    }
}