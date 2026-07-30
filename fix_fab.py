import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

old_fab = """                    floatingActionButton = {
                        if (currentTab == BottomTab.COMPRESS) {
                            FloatingActionButton(
                                onClick = { compressViewModel.startCompression() },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start Compression")
                            }
                        } else if (currentTab == BottomTab.RULES) {
                            FloatingActionButton(
                                onClick = { rulesViewModel.showRuleDialogForNew() },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Add Rule")
                            }
                        }
                    }
                )"""

new_fab = """                    }
                )"""

content = content.replace(old_fab, new_fab)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "r") as f:
    c_content = f.read()

if "import androidx.compose.material3.Scaffold" not in c_content:
    c_content = c_content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\nimport androidx.compose.material3.Scaffold\nimport androidx.compose.material3.FloatingActionButton\nimport androidx.compose.material.icons.rounded.PlayArrow")

c_old = """    LazyColumn(
        modifier = modifier.fillMaxSize(),"""

c_new = """    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCompression() },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start Compression")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(padding),"""

c_content = c_content.replace(c_old, c_new)
c_content += "\n}"

with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "w") as f:
    f.write(c_content)

