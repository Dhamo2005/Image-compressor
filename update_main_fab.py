import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material3.Scaffold", "import androidx.compose.material3.Scaffold\nimport androidx.compose.material3.FloatingActionButton\nimport androidx.compose.material.icons.rounded.PlayArrow")

# Find Scaffold
old_scaffold_bottom = """                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { mainViewModel.selectTab(it) }
                        )
                    }
                )"""

new_scaffold_bottom = """                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { mainViewModel.selectTab(it) }
                        )
                    },
                    floatingActionButton = {
                        if (currentTab == BottomTab.COMPRESS) {
                            FloatingActionButton(
                                onClick = { compressViewModel.startCompression() },
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start Compression")
                            }
                        }
                    }
                )"""

content = content.replace(old_scaffold_bottom, new_scaffold_bottom)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
