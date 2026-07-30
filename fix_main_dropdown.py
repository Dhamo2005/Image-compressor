with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

old_actions = """                            actions = {
                                androidx.compose.material3.IconButton(onClick = { showMenu = true }) {
                                    androidx.compose.material3.Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                                }
                            },"""

new_actions = """                            actions = {
                                androidx.compose.material3.IconButton(onClick = { showMenu = true }) {
                                    androidx.compose.material3.Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                                }
                                androidx.compose.material3.DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("About") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showAboutDialog.value = true
                                        }
                                    )
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("Help & Tutorial") },
                                        onClick = {
                                            showMenu = false
                                            mainViewModel.showHelpTutorial.value = true
                                        }
                                    )
                                }
                            },"""

content = content.replace(old_actions, new_actions)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
