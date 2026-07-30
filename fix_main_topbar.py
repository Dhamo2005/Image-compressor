import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Replace the Scaffold topBar
old_scaffold_top = """                var showMenu by remember { mutableStateOf(false) }
                val historyQuery by historyViewModel.searchQuery.collectAsStateWithLifecycle()

                val topBarTitle = when (currentTab) {
                    BottomTab.COMPRESS -> "Compress Documents"
                    BottomTab.RULES -> "Document Rules"
                    BottomTab.HISTORY -> "Search history..."
                    BottomTab.SETTINGS -> "Settings"
                }

                Scaffold(
                    topBar = {
                        Column {
                            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                            GmailTopBar(
                                title = topBarTitle,
                                searchQuery = if (currentTab == BottomTab.HISTORY) historyQuery else "",
                                onSearchQueryChanged = { if (currentTab == BottomTab.HISTORY) historyViewModel.searchQuery.value = it },
                                onMenuClick = { showMenu = true }
                            )
                        }
                    },"""

new_scaffold_top = """                var showMenu by remember { mutableStateOf(false) }

                val topBarTitle = when (currentTab) {
                    BottomTab.COMPRESS -> "Compress Documents"
                    BottomTab.RULES -> "Document Rules"
                    BottomTab.HISTORY -> "Compression History"
                    BottomTab.SETTINGS -> "Settings"
                }

                Scaffold(
                    topBar = {
                        androidx.compose.material3.TopAppBar(
                            title = { Text(topBarTitle, fontWeight = FontWeight.Bold) },
                            actions = {
                                androidx.compose.material3.IconButton(onClick = { showMenu = true }) {
                                    androidx.compose.material3.Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },"""

content = content.replace(old_scaffold_top, new_scaffold_top)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
