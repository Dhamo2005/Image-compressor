import re

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("@androidx.compose.foundation.ExperimentalFoundationApi\n@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)", "@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)")

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(content)
