import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.rounded.PlayArrow", "import androidx.compose.material.icons.rounded.PlayArrow\nimport androidx.compose.material.icons.rounded.Add")

old_fab = """                    floatingActionButton = {
                        if (currentTab == BottomTab.COMPRESS) {
                            FloatingActionButton(
                                onClick = { compressViewModel.startCompression() },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start Compression")
                            }
                        }
                    }"""

new_fab = """                    floatingActionButton = {
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
                    }"""

content = content.replace(old_fab, new_fab)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
