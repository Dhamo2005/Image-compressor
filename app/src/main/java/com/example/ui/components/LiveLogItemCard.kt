package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.LogItem
import com.example.model.LogType

@Composable
fun LiveLogItemCard(
    log: LogItem,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (log.logType) {
        LogType.SUCCESS -> Icons.Rounded.CheckCircle to MaterialTheme.colorScheme.primary
        LogType.WARNING -> Icons.Rounded.Warning to MaterialTheme.colorScheme.tertiary
        LogType.ERROR -> Icons.Rounded.Error to MaterialTheme.colorScheme.error
        LogType.INFO -> Icons.Rounded.Info to MaterialTheme.colorScheme.secondary
    }

    ListItem(
        modifier = modifier.fillMaxWidth(),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = log.logType.name,
                    tint = color
                )
            }
        },
        headlineContent = {
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}
