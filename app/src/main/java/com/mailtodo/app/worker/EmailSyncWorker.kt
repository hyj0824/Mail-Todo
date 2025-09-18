package com.mailtodo.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mailtodo.app.repository.MailTodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmailSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MailTodoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Get all active accounts and sync emails
            val accounts = repository.getActiveAccounts()

            // Note: In a real implementation, you'd need to collect the flow
            // For now, this is a simplified version

            // Process emails for date extraction
            repository.processEmailsForDates()

            // Sync todos with Microsoft
            repository.syncTodosWithMicrosoft()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
