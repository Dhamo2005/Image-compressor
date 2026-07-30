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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Rule
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DocumentRule
import com.example.ui.components.DocumentRuleDialog
import com.example.viewmodel.RulesViewModel
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material.icons.automirrored.rounded.Rule

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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Matches student filenames automatically",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        editingRule = null
                        showRuleDialog = true
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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

        // Rules List
        items(items = rules, key = { it.id }) { rule ->
            val displaySize = if (rule.targetSizeKb >= 1024) {
                val mb = rule.targetSizeKb / 1024f
                if (rule.targetSizeKb % 1024 == 0) "${rule.targetSizeKb / 1024} MB" else "%.1f MB".format(mb)
            } else {
                "${rule.targetSizeKb} KB"
            }
            
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        editingRule = rule
                        showRuleDialog = true
                    },
                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Rule,
                        contentDescription = "Rule Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                headlineContent = {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = "$displaySize • ${rule.outputFormat.name} • ${rule.minQuality}%-${rule.maxQuality}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = rule.enabled,
                            onCheckedChange = { viewModel.toggleRuleEnabled(rule) }
                        )
                    }
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 56.dp))
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(code)
