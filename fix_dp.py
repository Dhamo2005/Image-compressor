import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.unit.dp")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

