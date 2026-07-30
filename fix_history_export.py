import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

# Remove showExportDialog state
content = re.sub(r'\s*var showExportDialog by remember \{ mutableStateOf\(false\) \}', '', content)

# Remove the Export CSV button
export_button_regex = re.compile(r'\s*IconButton\(onClick = \{ showExportDialog = true \}\) \{\s*Icon\(Icons\.Rounded\.FileDownload, contentDescription = "Export CSV", tint = MaterialTheme\.colorScheme\.primary\)\s*\}', re.DOTALL)
content = export_button_regex.sub('', content)

# Remove the ExportReportDialog usage at the bottom
export_dialog_regex = re.compile(r'\s*if \(showExportDialog\) \{\s*com\.example\.ui\.components\.ExportReportDialog\(\s*onDismiss = \{ showExportDialog = false \},\s*onExport = \{ \[^}]+\}\s*\)\s*\}', re.DOTALL)
content = export_dialog_regex.sub('', content)

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)
