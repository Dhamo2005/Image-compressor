import re

# Fix CompressScreen
with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "r") as f:
    c_content = f.read()
if c_content.endswith("}\n}"):
    c_content = c_content[:-2]
with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "w") as f:
    f.write(c_content)

# Update RulesScreen
with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    r_content = f.read()

if "import androidx.compose.material3.Scaffold" not in r_content:
    r_content = r_content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\nimport androidx.compose.material3.Scaffold\nimport androidx.compose.material3.FloatingActionButton\nimport androidx.compose.material.icons.rounded.Add")

r_old = """    LazyColumn(
        modifier = modifier.fillMaxSize(),"""

r_new = """    Scaffold(
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
            modifier = modifier.fillMaxSize().padding(padding),"""

r_content = r_content.replace(r_old, r_new)

# Remove the Top Action Bar since we have GmailTopBar and a FAB
top_action_bar = """        // Top Action Bar
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }"""

r_content = r_content.replace(top_action_bar, "")
r_content += "\n}"

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(r_content)
