#!/bin/bash
cat << 'INNER_EOF' > script.py
import re
with open("app/src/main/java/com/example/viewmodel/CompressViewModel.kt", "r") as f:
    content = f.read()

replacement = """import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class CompressViewModel"""

content = content.replace("class CompressViewModel", replacement)

log_replacement = """val liveLogs: StateFlow<List<LogItem>> = activeJobId
        .flatMapLatest { jobId -> 
            if (jobId == null) flowOf(emptyList()) 
            else compressionRepository.getLogsForJob(jobId) 
        }
        .stateIn("""

content = re.sub(r'val liveLogs: StateFlow<List<LogItem>> = compressionRepository\.allLogs\s*\.stateIn\(', log_replacement, content)

with open("app/src/main/java/com/example/viewmodel/CompressViewModel.kt", "w") as f:
    f.write(content)
INNER_EOF
python3 script.py
