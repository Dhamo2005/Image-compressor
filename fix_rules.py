import re

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.clickable", "import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.ExperimentalFoundationApi\nimport androidx.compose.foundation.combinedClickable\nimport androidx.compose.material.icons.rounded.Close")

old_scaffold = """    Scaffold(
        floatingActionButton = {"""

new_scaffold = """    var selectedRuleIds by remember { mutableStateOf<Set<String>>(emptySet()) }

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
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            }
        },
        floatingActionButton = {"""

content = content.replace(old_scaffold, new_scaffold)

# Need to update modifier of ListItem
old_modifier = """                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        editingRule = rule
                        showRuleDialog = true
                    },"""

new_modifier = """                modifier = Modifier
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
                    ),"""

content = content.replace(old_modifier, new_modifier)

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(content)
