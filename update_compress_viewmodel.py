import re

with open("app/src/main/java/com/example/viewmodel/CompressViewModel.kt", "r") as f:
    content = f.read()

old_start = """    fun startCompression() {
        val src = sourceUri.value ?: return
        val dst = destUri.value ?: return

        viewModelScope.launch {
            val jobId = compressionRepository.startBatchCompression(src, dst)
            activeJobId.value = jobId
        }
    }"""

new_start = """    fun startCompression() {
        val src = sourceUri.value ?: return
        val dst = destUri.value ?: return

        viewModelScope.launch {
            val isEmpty = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                StorageHelper.scanStudentFolders(getApplication(), src).isEmpty()
            }
            if (isEmpty) {
                android.widget.Toast.makeText(getApplication(), "No student folders found in source directory.", android.widget.Toast.LENGTH_LONG).show()
                return@launch
            }
            val jobId = compressionRepository.startBatchCompression(src, dst)
            activeJobId.value = jobId
        }
    }"""

content = content.replace(old_start, new_start)

with open("app/src/main/java/com/example/viewmodel/CompressViewModel.kt", "w") as f:
    f.write(content)
