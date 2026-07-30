import re

code = """package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.JobStatus
import com.example.viewmodel.CompressViewModel

@Composable
fun CompressScreen(
    viewModel: CompressViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sourceUri by viewModel.sourceUri.collectAsStateWithLifecycle()
    val destUri by viewModel.destUri.collectAsStateWithLifecycle()
    val sourceName by viewModel.sourceDisplayName.collectAsStateWithLifecycle()
    val destName by viewModel.destDisplayName.collectAsStateWithLifecycle()
    val activeJob by viewModel.activeJob.collectAsStateWithLifecycle()

    val sourceFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.setSourceFolder(uri)
        }
    }

    val destFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.setDestFolder(uri)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "Folder Selection",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
            )
        }

        item {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { sourceFolderLauncher.launch(null) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = "Source",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                headlineContent = {
                    Text(
                        text = "Source Folder",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                supportingContent = {
                    Text(
                        text = sourceName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        item {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { destFolderLauncher.launch(null) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.CreateNewFolder,
                        contentDescription = "Destination",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                headlineContent = {
                    Text(
                        text = "Destination Folder",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                supportingContent = {
                    Text(
                        text = destName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        if (activeJob != null) {
            val job = activeJob!!
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(
                    text = "Active Job",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
                )
                
                Column(modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Status: ${job.status.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (job.status == JobStatus.RUNNING) {
                            Button(
                                onClick = { viewModel.cancelActiveJob() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Rounded.Cancel, contentDescription = "Cancel")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val total = job.totalImages
                    val processed = job.processedImages + job.failedImages + job.skippedImages
                    val isScanning = job.status == JobStatus.RUNNING && total == 0
                    val progress = if (total > 0) processed.toFloat() / total.toFloat() else 0f

                    if (isScanning) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isScanning) "Scanning folder..." else "$processed/$total completed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isScanning) "..." else "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "w") as f:
    f.write(code)
