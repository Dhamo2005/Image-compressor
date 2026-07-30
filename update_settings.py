import re

code = """package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ListItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.preferences.AppTheme
import com.example.viewmodel.RulesViewModel
import com.example.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    rulesViewModel: RulesViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val imageSettings by viewModel.imageSettings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val rulesJsonStr = rulesViewModel.exportRulesJson()
                    val rootObj = JSONObject().apply {
                        put("rules", org.json.JSONArray(rulesJsonStr))
                        put("theme", theme.name)
                        put("imageSettings", JSONObject().apply {
                            put("allowResize", imageSettings.allowResize)
                            put("jpegOptimization", imageSettings.jpegOptimization)
                            put("maxQuality", imageSettings.maxQuality)
                            put("minQuality", imageSettings.minQuality)
                            put("defaultTargetSizeKb", imageSettings.defaultTargetSizeKb)
                        })
                    }
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use {
                            it.write(rootObj.toString(2).toByteArray())
                        }
                    }
                    Toast.makeText(context, "Exported successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val jsonStr = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    }
                    if (jsonStr != null) {
                        val rootObj = JSONObject(jsonStr)
                        if (rootObj.has("rules")) {
                            rulesViewModel.importRulesJson(rootObj.getJSONArray("rules").toString())
                        }
                        if (rootObj.has("theme")) {
                            runCatching { viewModel.setTheme(AppTheme.valueOf(rootObj.getString("theme"))) }
                        }
                        if (rootObj.has("imageSettings")) {
                            val isObj = rootObj.getJSONObject("imageSettings")
                            viewModel.updateImageSettings(
                                imageSettings.copy(
                                    allowResize = isObj.optBoolean("allowResize", imageSettings.allowResize),
                                    jpegOptimization = isObj.optBoolean("jpegOptimization", imageSettings.jpegOptimization),
                                    maxQuality = isObj.optInt("maxQuality", imageSettings.maxQuality),
                                    minQuality = isObj.optInt("minQuality", imageSettings.minQuality),
                                    defaultTargetSizeKb = isObj.optInt("defaultTargetSizeKb", imageSettings.defaultTargetSizeKb)
                                )
                            )
                        }
                        Toast.makeText(context, "Imported successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "General",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 16.dp, bottom = 8.dp)
            )
        }
        item {
            ListItem(
                leadingContent = { Icon(Icons.Rounded.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                headlineContent = { Text("App Theme") },
                supportingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        AppTheme.values().forEach { t ->
                            androidx.compose.material3.FilterChip(
                                selected = theme == t,
                                onClick = { viewModel.setTheme(t) },
                                label = { Text(t.name) }
                            )
                        }
                    }
                }
            )
        }
        
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Compression Engine",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        
        item {
            var unitExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            var sizeUnit by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (imageSettings.defaultTargetSizeKb >= 1024 && imageSettings.defaultTargetSizeKb % 1024 == 0) "MB" else "KB") 
            }
            var targetSizeKbStr by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (sizeUnit == "MB") (imageSettings.defaultTargetSizeKb / 1024).toString() else imageSettings.defaultTargetSizeKb.toString()) 
            }

            ListItem(
                leadingContent = { Icon(Icons.Rounded.PhotoSizeSelectLarge, contentDescription = "Size", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                headlineContent = { Text("Global Target Size") },
                supportingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = targetSizeKbStr,
                            onValueChange = { newValue ->
                                targetSizeKbStr = newValue
                                val sizeVal = newValue.toIntOrNull() ?: 300
                                val finalSizeKb = if (sizeUnit == "MB") sizeVal * 1024 else sizeVal
                                viewModel.updateImageSettings(imageSettings.copy(defaultTargetSizeKb = finalSizeKb))
                            },
                            label = { Text("Target Max Size") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(2f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        androidx.compose.material3.ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            androidx.compose.material3.OutlinedTextField(
                                value = sizeUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            androidx.compose.material3.ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                listOf("KB", "MB").forEach { unit ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            sizeUnit = unit
                                            unitExpanded = false
                                            val sizeVal = targetSizeKbStr.toIntOrNull() ?: 300
                                            val finalSizeKb = if (unit == "MB") sizeVal * 1024 else sizeVal
                                            viewModel.updateImageSettings(imageSettings.copy(defaultTargetSizeKb = finalSizeKb))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        
        item {
            ListItem(
                leadingContent = { Icon(Icons.Rounded.HighQuality, contentDescription = "Quality", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                headlineContent = { Text("Quality Search Bounds") },
                supportingContent = {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "Max Quality: ${imageSettings.maxQuality}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = imageSettings.maxQuality.toFloat(),
                            onValueChange = { viewModel.updateImageSettings(imageSettings.copy(maxQuality = it.toInt())) },
                            valueRange = 50f..100f
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Min Quality: ${imageSettings.minQuality}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = imageSettings.minQuality.toFloat(),
                            onValueChange = { viewModel.updateImageSettings(imageSettings.copy(minQuality = it.toInt())) },
                            valueRange = 5f..50f
                        )
                    }
                }
            )
        }
        
        item {
            ListItem(
                headlineContent = { Text("Allow Image Resizing") },
                trailingContent = {
                    Switch(
                        checked = imageSettings.allowResize,
                        onCheckedChange = { viewModel.updateImageSettings(imageSettings.copy(allowResize = it)) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("JPEG Optimization & EXIF") },
                trailingContent = {
                    Switch(
                        checked = imageSettings.jpegOptimization,
                        onCheckedChange = { viewModel.updateImageSettings(imageSettings.copy(jpegOptimization = it)) }
                    )
                }
            )
        }
        
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Data & Storage",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        
        item {
            ListItem(
                leadingContent = { Icon(Icons.Rounded.SettingsBackupRestore, contentDescription = "Backup", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                headlineContent = { Text("Backup & Restore Rules") },
                supportingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = "Import", modifier = Modifier.padding(end = 8.dp).width(18.dp))
                            Text("Import")
                        }
                        Button(
                            onClick = { exportLauncher.launch("image_compressor_backup.json") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Upload, contentDescription = "Export", modifier = Modifier.padding(end = 8.dp).width(18.dp))
                            Text("Export")
                        }
                    }
                }
            )
        }
    }
}
"""
with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(code)
