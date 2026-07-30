import re

# Update LiveLogItemCard
with open("app/src/main/java/com/example/ui/components/LiveLogItemCard.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.layout.padding", "import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.background\nimport androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.size\nimport androidx.compose.foundation.shape.CircleShape\nimport androidx.compose.ui.draw.clip")

old_icon = """            Icon(
                imageVector = icon,
                contentDescription = log.logType.name,
                tint = color
            )"""

new_icon = """            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = log.logType.name,
                    tint = color
                )
            }"""

content = content.replace(old_icon, new_icon)

with open("app/src/main/java/com/example/ui/components/LiveLogItemCard.kt", "w") as f:
    f.write(content)

