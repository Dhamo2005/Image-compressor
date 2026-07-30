with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if "LazyColumn(" in line and not skip:
        # Re-write from LazyColumn down to Compression Engine Header
        new_lines.append(line)
        new_lines.append("        modifier = modifier.fillMaxSize(),\n")
        new_lines.append("        contentPadding = PaddingValues(vertical = 8.dp)\n")
        new_lines.append("    ) {\n")
        new_lines.append("        item {\n")
        new_lines.append("            Text(\n")
        new_lines.append('                text = "Compression Engine",\n')
        new_lines.append("                style = MaterialTheme.typography.titleMedium,\n")
        new_lines.append("                color = MaterialTheme.colorScheme.primary,\n")
        new_lines.append("                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)\n")
        new_lines.append("            )\n")
        new_lines.append("        }\n")
        skip = True
    elif "var unitExpanded" in line and skip:
        skip = False
        new_lines.append(line)
    elif not skip:
        new_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.writelines(new_lines)

