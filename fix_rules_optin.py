import re

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("@Composable\nfun RulesScreen", "@androidx.compose.foundation.ExperimentalFoundationApi\n@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun RulesScreen")

content = content.replace("import androidx.compose.material3.TopAppBar", "import androidx.compose.material3.TopAppBar\nimport androidx.compose.material3.surfaceColorAtElevation")

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(content)
