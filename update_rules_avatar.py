import re

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.layout.padding", "import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.background\nimport androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.shape.CircleShape\nimport androidx.compose.ui.draw.clip")

old_icon = """                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Rule,
                        contentDescription = "Rule Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },"""

new_icon = """                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Rule,
                            contentDescription = "Rule Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },"""

content = content.replace(old_icon, new_icon)

with open("app/src/main/java/com/example/ui/screens/RulesScreen.kt", "w") as f:
    f.write(content)
