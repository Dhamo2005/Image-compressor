import re

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)", "MaterialTheme.colorScheme.surfaceContainerHigh")

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(content)
