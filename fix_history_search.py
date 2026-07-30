import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

old_filter = """        // Filter Chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {"""

new_filter = """        item {
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Search by student name or filename") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
        }

        // Filter Chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {"""

content = content.replace(old_filter, new_filter)

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)
