import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.components.BottomNavBar", "import com.example.ui.components.BottomNavBar\nimport com.example.ui.components.GmailTopBar\nimport androidx.compose.foundation.layout.Column\nimport androidx.compose.foundation.layout.Spacer\nimport androidx.compose.foundation.layout.height\nimport androidx.compose.foundation.layout.WindowInsets\nimport androidx.compose.foundation.layout.safeDrawing")

# find Scaffold
old_scaffold = """                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Image Compressor Pro",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                IconButton(onClick = { showMenu = !showMenu }) {
                                    Icon(Icons.Rounded.MoreVert, contentDescription = "More")
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("About") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showAboutDialog.value = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Help & Tutorial") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showHelpTutorial.value = true
                                        }
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { mainViewModel.selectTab(it) }
                        )
                    }
                )"""

new_scaffold = """                Scaffold(
                    topBar = {
                        Column {
                            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                            GmailTopBar(
                                title = "Search in compressor",
                                onMenuClick = { showMenu = true }
                            )
                        }
                    },
                    bottomBar = {
                        BottomNavBar(
                            selectedTab = currentTab,
                            onTabSelected = { mainViewModel.selectTab(it) }
                        )
                    }
                )"""

content = content.replace(old_scaffold, new_scaffold)

# Need to place the DropdownMenu outside the TopBar since it's removed
menu_code = """                                // Global Dialogs
                if (showMenu) {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.padding(top = 64.dp, start = 16.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                showMenu = false
                                mainViewModel.showAboutDialog.value = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help & Tutorial") },
                            onClick = {
                                showMenu = false
                                mainViewModel.showHelpTutorial.value = true
                            }
                        )
                    }
                }

                if (showAbout) {"""
content = content.replace("                // Global Dialogs\n                if (showAbout) {", menu_code)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
