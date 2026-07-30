import re

with open("app/src/main/java/com/example/workers/BatchCompressionWorker.kt", "r") as f:
    content = f.read()

old_info = """    private fun createForegroundInfo(contentText: String, progress: Int, max: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Image Compressor Pro")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setOngoing(true)
            .setProgress(max, progress, false)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }"""

new_info = """    private fun createForegroundInfo(contentText: String, progress: Int, max: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Image Compressor Pro")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setOngoing(true)
            .setProgress(max, progress, false)
            .build()
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID, 
                notification, 
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }"""

content = content.replace(old_info, new_info)

with open("app/src/main/java/com/example/workers/BatchCompressionWorker.kt", "w") as f:
    f.write(content)

