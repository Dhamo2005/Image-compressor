package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FolderSpecial
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerContent(
    onOpenAbout: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenProfiles: () -> Unit,
    onCreateSampleFolder: () -> Unit,
    onOpenCameraScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier.width(300.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Image Compressor Pro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Preserves student folder structures",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = onOpenAbout,
            icon = { Icon(Icons.Rounded.Info, contentDescription = "About") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Help & Tutorial") },
            selected = false,
            onClick = onOpenHelp,
            icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Export Reports") },
            selected = false,
            onClick = onOpenExport,
            icon = { Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Profiles & Presets") },
            selected = false,
            onClick = onOpenProfiles,
            icon = { Icon(Icons.Rounded.Style, contentDescription = "Profiles") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "BONUS TOOLS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        NavigationDrawerItem(
            label = { Text("Create Sample Folders") },
            selected = false,
            onClick = onCreateSampleFolder,
            icon = { Icon(Icons.Rounded.FolderSpecial, contentDescription = "Sample Folders") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("Camera Document Scan") },
            selected = false,
            onClick = onOpenCameraScanner,
            icon = { Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera Scanner") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
