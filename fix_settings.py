import re

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

# Remove AppTheme UI
theme_regex = re.compile(r'\s*item \{\s*ListItem\(\s*leadingContent = \{ Icon\(Icons\.Rounded\.BrightnessMedium.*?\}\s*\)\s*\}\s*item \{\s*ListItem\(\s*leadingContent = \{ Icon\(Icons\.Rounded\.PhotoSizeSelectLarge', re.DOTALL)
content = theme_regex.sub(r'\n\n        item {\n            var unitExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }', content)

# Remove AppTheme import if not needed
# Actually, the replacement just needs to be precise.

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
