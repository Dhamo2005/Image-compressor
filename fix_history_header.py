import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

old_history_header = """                Text(
                    text = "Compression History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )"""

new_history_header = """                Text(
                    text = "Compression History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )"""

content = content.replace(old_history_header, new_history_header)

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)
