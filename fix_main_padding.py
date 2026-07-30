with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

old_box = """                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {"""

new_box = """                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .androidx.compose.foundation.layout.consumeWindowInsets(innerPadding)
                    ) {"""

content = content.replace(old_box, new_box)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
