import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

old_scaffold_top = """                val showMenu by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        Column {
                            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                            GmailTopBar(
                                title = "Search in compressor",
                                onMenuClick = { showMenu = true }
                            )
                        }
                    },"""

new_scaffold_top = """                var showMenu by remember { mutableStateOf(false) }
                val historyQuery by historyViewModel.searchQuery.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        Column {
                            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                            GmailTopBar(
                                title = if (currentTab == BottomTab.HISTORY) "Search history..." else "Image Compressor Pro",
                                searchQuery = if (currentTab == BottomTab.HISTORY) historyQuery else "",
                                onSearchQueryChanged = { if (currentTab == BottomTab.HISTORY) historyViewModel.searchQuery.value = it },
                                onMenuClick = { showMenu = true }
                            )
                        }
                    },"""

# Note: The original code has "var showMenu by remember { mutableStateOf(false) }"
# So I should handle the regex carefully.

content = re.sub(r'var showMenu by remember \{ mutableStateOf\(false\) \}\s*Scaffold\(\s*topBar = \{\s*Column \{\s*Spacer\(modifier = Modifier\.windowInsetsPadding\(WindowInsets\.safeDrawing\)\)\s*GmailTopBar\(\s*title = "Search in compressor",\s*onMenuClick = \{ showMenu = true \}\s*\)\s*\}\s*\},', new_scaffold_top, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

