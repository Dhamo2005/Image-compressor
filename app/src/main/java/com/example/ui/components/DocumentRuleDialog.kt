package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.DocumentRule
import com.example.model.OutputFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentRuleDialog(
    initialRule: DocumentRule? = null,
    onDismiss: () -> Unit,
    onSave: (DocumentRule) -> Unit
) {
    var name by remember { mutableStateOf(initialRule?.name ?: "") }
    var sizeUnit by remember { mutableStateOf(if ((initialRule?.targetSizeKb ?: 0) >= 1024 && (initialRule?.targetSizeKb ?: 0) % 1024 == 0) "MB" else "KB") }
    var targetKbStr by remember { 
        mutableStateOf(
            if (sizeUnit == "MB") ((initialRule?.targetSizeKb ?: 296) / 1024).toString()
            else (initialRule?.targetSizeKb ?: 296).toString()
        ) 
    }
    var unitExpanded by remember { mutableStateOf(false) }
    var outputFormat by remember { mutableStateOf(initialRule?.outputFormat ?: OutputFormat.JPEG) }
    var enabled by remember { mutableStateOf(initialRule?.enabled ?: true) }
    var maxQualityStr by remember { mutableStateOf((initialRule?.maxQuality ?: 95).toString()) }
    var minQualityStr by remember { mutableStateOf((initialRule?.minQuality ?: 20).toString()) }

    var formatExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialRule == null) "Add Document Rule" else "Edit Document Rule") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Document Keyword (e.g. Marksheet)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = targetKbStr,
                        onValueChange = { targetKbStr = it },
                        label = { Text("Target Max Size") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    ExposedDropdownMenuBox(
                        expanded = unitExpanded,
                        onExpandedChange = { unitExpanded = !unitExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = sizeUnit,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                            modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = unitExpanded,
                            onDismissRequest = { unitExpanded = false }
                        ) {
                            listOf("KB", "MB").forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit) },
                                    onClick = {
                                        sizeUnit = unit
                                        unitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = formatExpanded,
                    onExpandedChange = { formatExpanded = !formatExpanded }
                ) {
                    OutlinedTextField(
                        value = outputFormat.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Output Format") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                        modifier = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = formatExpanded,
                        onDismissRequest = { formatExpanded = false }
                    ) {
                        OutputFormat.values().forEach { fmt ->
                            DropdownMenuItem(
                                text = { Text(fmt.name) },
                                onClick = {
                                    outputFormat = fmt
                                    formatExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = maxQualityStr,
                        onValueChange = { maxQualityStr = it },
                        label = { Text("Max Quality") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = minQualityStr,
                        onValueChange = { minQualityStr = it },
                        label = { Text("Min Quality") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enable Rule",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val sizeVal = targetKbStr.toIntOrNull() ?: 296
                        val finalSizeKb = if (sizeUnit == "MB") sizeVal * 1024 else sizeVal
                        val rule = DocumentRule(
                            id = initialRule?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name.trim(),
                            targetSizeKb = finalSizeKb,
                            outputFormat = outputFormat,
                            enabled = enabled,
                            maxQuality = maxQualityStr.toIntOrNull() ?: 95,
                            minQuality = minQualityStr.toIntOrNull() ?: 20
                        )
                        onSave(rule)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
