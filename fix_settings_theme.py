import re

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

# Remove Theme section from SettingsScreen
theme_section = re.compile(r'\s*item \{\s*ListItem\(\s*leadingContent = \{ Icon\(Icons\.Rounded\.Palette.*?\}\s*\)\s*\}', re.DOTALL)
content = theme_section.sub('', content)

# But wait, the previous code had:
# item {
#     ListItem(
#         leadingContent = { Icon(Icons.Rounded.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
#         headlineContent = { Text("App Theme") },
#         supportingContent = { ... }
#     )
# }
# Let's replace the whole item { ... Palette ... } exactly.

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
