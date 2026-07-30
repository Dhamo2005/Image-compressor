import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

content = content.replace("            items(items = logs, key = { it.id }) { log ->\n                LiveLogItemCard(log = log)\n            }", """            items(items = logs, key = { it.id }) { log ->
                LiveLogItemCard(log = log)
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(start = 56.dp))
            }""")

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)
