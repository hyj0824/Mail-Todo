package com.mailtodo.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mailtodo.app.worker.EmailSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailSyncService : Service() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 启动后台同步工作
        val syncWorkRequest = OneTimeWorkRequestBuilder<EmailSyncWorker>().build()
        workManager.enqueue(syncWorkRequest)

        return START_NOT_STICKY
    }
}
