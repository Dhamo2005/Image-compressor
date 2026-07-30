import re

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

old_storage_header = """            Text(
                text = "Data & Storage",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
            )"""

new_storage_header = """            Text(
                text = "Data & Storage",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )"""

content = content.replace(old_storage_header, new_storage_header)

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
