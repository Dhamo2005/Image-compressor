import re

with open("app/src/main/java/com/example/ui/components/GmailTopBar.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\nimport androidx.compose.foundation.text.BasicTextField\nimport androidx.compose.ui.graphics.SolidColor")

old_sig = """fun GmailTopBar(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {"""

new_sig = """fun GmailTopBar(
    title: String,
    searchQuery: String = "",
    onSearchQueryChanged: (String) -> Unit = {},
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {"""

content = content.replace(old_sig, new_sig)

old_text = """            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )"""

new_text = """            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }"""

content = content.replace(old_text, new_text)

with open("app/src/main/java/com/example/ui/components/GmailTopBar.kt", "w") as f:
    f.write(content)
