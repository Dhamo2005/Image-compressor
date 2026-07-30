package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.rounded.Close
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.material.icons.automirrored.rounded.Rule

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
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

    var selectedRuleIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    Scaffold(
        topBar = {
            if (selectedRuleIds.isNotEmpty()) {
                androidx.compose.material3.TopAppBar(
                    title = { Text("${selectedRuleIds.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { selectedRuleIds = emptySet() }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            selectedRuleIds.forEach { id ->
                                rules.find { it.id == id }?.let { viewModel.deleteRule(it) }
                            }
                            selectedRuleIds = emptySet()
                        }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete selected")
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingRule = null
                    showRuleDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Rule")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {


        // Rules List
        items(items = rules, key = { it.id }) { rule ->
            var expanded by remember { mutableStateOf(false) }
            val displaySize = if (rule.targetSizeKb >= 1024) {
                val mb = rule.targetSizeKb / 1024f
                if (rule.targetSizeKb % 1024 == 0) "${rule.targetSizeKb / 1024} MB" else "%.1f MB".format(mb)
            } else {
                "${rule.targetSizeKb} KB"
            }
            
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (selectedRuleIds.contains(rule.id)) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent)
                    .combinedClickable(
                        onClick = { 
                            if (selectedRuleIds.isNotEmpty()) {
                                if (selectedRuleIds.contains(rule.id)) {
                                    selectedRuleIds = selectedRuleIds - rule.id
                                } else {
                                    selectedRuleIds = selectedRuleIds + rule.id
                                }
                            } else {
                                editingRule = rule
                                showRuleDialog = true
                            }
                        },
                        onLongClick = {
                            if (selectedRuleIds.contains(rule.id)) {
                                selectedRuleIds = selectedRuleIds - rule.id
                            } else {
                                selectedRuleIds = selectedRuleIds + rule.id
                            }
                        }
                    ),
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Rule,
                            contentDescription = "Rule Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
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
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = {
                                    expanded = false
                                    viewModel.duplicateRule(rule)
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Duplicate")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    expanded = false
                                    viewModel.deleteRule(rule)
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 56.dp))
        }
    }
}

}