import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.layout.safeDrawing", "import androidx.compose.foundation.layout.safeDrawing\nimport androidx.compose.foundation.layout.windowInsetsPadding")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
