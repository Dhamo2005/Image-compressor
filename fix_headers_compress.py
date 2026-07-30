import re

with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "r") as f:
    content = f.read()

old_header = """            Text(
                text = "Folder Selection",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
            )"""

new_header = """            Text(
                text = "Folder Selection",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )"""

content = content.replace(old_header, new_header)

old_job_header = """                Text(
                    text = "Active Job",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 72.dp, top = 8.dp, bottom = 8.dp)
                )"""

new_job_header = """                Text(
                    text = "Active Job",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )"""

content = content.replace(old_job_header, new_job_header)

# Fix indentation for job details
old_col = """                Column(modifier = Modifier.padding(horizontal = 72.dp, vertical = 8.dp)) {"""
new_col = """                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {"""
content = content.replace(old_col, new_col)

with open("app/src/main/java/com/example/ui/screens/CompressScreen.kt", "w") as f:
    f.write(content)
