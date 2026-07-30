with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.layout.padding", "import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.consumeWindowInsets")
content = content.replace(".androidx.compose.foundation.layout.consumeWindowInsets(innerPadding)", ".consumeWindowInsets(innerPadding)")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
