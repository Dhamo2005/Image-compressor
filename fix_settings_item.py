with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if "var unitExpanded by" in line:
        new_lines.append("        item {\n")
        new_lines.append(line)
    elif "            )\n" in line and "        item {" in new_lines[-10]:
        pass # this won't work simply
    else:
        new_lines.append(line)

# Let's use a better regex
