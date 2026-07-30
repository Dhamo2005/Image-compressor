with open("app/src/main/java/com/example/database/AppDatabase.kt", "r") as f:
    content = f.read()

content = content.replace(".build()", ".fallbackToDestructiveMigration().build()")

with open("app/src/main/java/com/example/database/AppDatabase.kt", "w") as f:
    f.write(content)
