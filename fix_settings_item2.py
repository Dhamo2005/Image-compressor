with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

bad_snippet = """            var unitExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            var sizeUnit by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (imageSettings.defaultTargetSizeKb >= 1024 && imageSettings.defaultTargetSizeKb % 1024 == 0) "MB" else "KB") 
            }
            var targetSizeKbStr by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (sizeUnit == "MB") (imageSettings.defaultTargetSizeKb / 1024).toString() else imageSettings.defaultTargetSizeKb.toString()) 
            }

            ListItem(
                leadingContent = { Icon(Icons.Rounded.PhotoSizeSelectLarge, contentDescription = "Size", tint = MaterialTheme.colorScheme.onSurfaceVariant) },"""

good_snippet = """        item {
            var unitExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            var sizeUnit by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (imageSettings.defaultTargetSizeKb >= 1024 && imageSettings.defaultTargetSizeKb % 1024 == 0) "MB" else "KB") 
            }
            var targetSizeKbStr by androidx.compose.runtime.remember(imageSettings.defaultTargetSizeKb) { 
                androidx.compose.runtime.mutableStateOf(if (sizeUnit == "MB") (imageSettings.defaultTargetSizeKb / 1024).toString() else imageSettings.defaultTargetSizeKb.toString()) 
            }

            ListItem(
                leadingContent = { Icon(Icons.Rounded.PhotoSizeSelectLarge, contentDescription = "Size", tint = MaterialTheme.colorScheme.onSurfaceVariant) },"""

content = content.replace(bad_snippet, good_snippet)

# We also need to close the `item {` block after the ListItem. 
# The ListItem ends at line 220.
# Let's find:
#                                 }
#                             }
#                         }
#                     }
#                 }
#             )

old_close = """                                }
                            }
                        }
                    }
                }
            )
        }
        
        item {"""

new_close = """                                }
                            }
                        }
                    }
                }
            )
        }
        
        item {"""
# wait, if it already had `        }` from before? Let's check the cat output.
# Line 220:             )
# Line 221:         }
# Wait! line 221 is a `}`, which means it WAS closed!
# Oh, my `fix_settings_head.py` just skipped until `var unitExpanded` but it skipped the `item {` that was previously BEFORE `var unitExpanded`!
# So the `}` is still there!

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
