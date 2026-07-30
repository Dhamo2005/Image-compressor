import re

code = """package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DocumentRule
import com.example.ui.components.DocumentRuleDialog
import com.example.viewmodel.RulesViewModel
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.foundation.layout.size

@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    modifier: Modifier = Modifier
) {
    val rules by viewModel.rules.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()

    var showRuleDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<DocumentRule?>(null) }

    if (showRuleDialog) {
        DocumentRuleDialog(
            initialRule = editingRule,
            onDismiss = {
                showRuleDialog = false
                editingRule = null
            },
            onSave = { rule ->
                if (editingRule == null) {
                    viewModel.addRule(rule)
                } else {
                    viewModel.updateRule(rule)
                }
                showRuleDialog = false
                editingRule = null
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Document Rules",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Matches student filenames automatically",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        editingRule = null
                        showRuleDialog = true
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Rule", fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Preset Profiles Quick Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Available Presets (${profiles.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "College Admission (296KB), Government/UPSC (50KB/20KB), Passport Specifications",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rules List
        items(items = rules, key = { it.id }) { rule ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        editingRule = rule
                        showRuleDialog = true
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    val displaySize = if (rule.targetSizeKb >= 1024) {
                        val mb = rule.targetSizeKb / 1024f
                        if (rule.targetSizeKb % 1024 == 0) "${rule.targetSizeKb / 1024} MB" else "%.1f MB".format(mb)
                    } else {
                        "${rule.targetSizeKb} KB"
                    }
                    Text(
                        text = "Target: $displaySize • Format: ${rule.outputFormat.name} • Quality: ${rule.minQuality}%-${rule.maxQuality}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = rule.enabled,
                    onCheckedChange = { viewModel.toggleRuleEnabled(rule) }
                )
                IconButton(onClick = { viewModel.duplicateRule(rule) }) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Duplicate", tint = Color.White.copy(alpha = 0.8f))
                }
                IconButton(onClick = { viewModel.deleteRule(rule) }) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(code)
