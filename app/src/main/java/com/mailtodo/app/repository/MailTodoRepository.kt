package com.mailtodo.app.repository

import com.mailtodo.app.data.dao.EmailDao
import com.mailtodo.app.data.dao.EmailAccountDao
import com.mailtodo.app.data.dao.TodoDao
import com.mailtodo.app.data.model.*
import com.mailtodo.app.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MailTodoRepository @Inject constructor(
    private val emailDao: EmailDao,
    private val emailAccountDao: EmailAccountDao,
    private val todoDao: TodoDao,
    private val emailService: EmailService,
    private val dateExtractionService: DateExtractionService,
    private val emailSummaryService: EmailSummaryService,
    private val microsoftTodoService: MicrosoftTodoService
) {

    // Email Account operations
    fun getActiveAccounts(): Flow<List<EmailAccount>> = emailAccountDao.getActiveAccounts()

    suspend fun addEmailAccount(account: EmailAccount) {
        emailAccountDao.insertAccount(account)
    }

    suspend fun updateEmailAccount(account: EmailAccount) {
        emailAccountDao.updateAccount(account)
    }

    suspend fun deleteEmailAccount(account: EmailAccount) {
        emailAccountDao.deleteAccount(account)
    }

    // Email operations
    fun getEmailsByAccount(accountId: String): Flow<List<Email>> = emailDao.getEmailsByAccount(accountId)

    suspend fun syncEmails(accountId: String) {
        val account = emailAccountDao.getAccountById(accountId) ?: return
        val emails = emailService.fetchEmails(account)
        emailDao.insertEmails(emails)

        // Update last sync time
        emailAccountDao.updateAccount(account.copy(lastSyncTime = Date()))
    }

    suspend fun processEmailsForDates() {
        val unprocessedEmails = emailDao.getUnprocessedEmailsWithDates()

        for (email in unprocessedEmails) {
            try {
                // Extract dates
                val extractedDates = dateExtractionService.extractDatesFromText(email.content)

                // Generate summary
                val summary = emailSummaryService.summarizeEmail(email)

                // Update email with processed information
                val updatedEmail = email.copy(
                    extractedDates = if (extractedDates.isNotEmpty()) {
                        extractedDates.joinToString(";") { "${it.dateText}|${it.parsedDate.time}|${it.context}" }
                    } else null,
                    summary = summary,
                    isProcessed = true
                )

                emailDao.updateEmail(updatedEmail)

                // Create todo items for emails with dates
                if (extractedDates.isNotEmpty()) {
                    createTodoFromEmail(updatedEmail, extractedDates)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Mark as processed even if failed to avoid infinite retry
                emailDao.updateEmail(email.copy(isProcessed = true))
            }
        }
    }

    private suspend fun createTodoFromEmail(email: Email, extractedDates: List<ExtractedDate>) {
        val earliestDate = extractedDates.minByOrNull { it.parsedDate }?.parsedDate

        val todoItem = TodoItem(
            id = UUID.randomUUID().toString(),
            title = email.subject.take(100), // Limit title length
            description = email.summary,
            dueDate = earliestDate,
            emailUid = email.uid,
            createdAt = Date()
        )

        todoDao.insertTodo(todoItem)
    }

    // Todo operations
    fun getAllTodos(): Flow<List<TodoItem>> = todoDao.getAllTodos()

    suspend fun addTodo(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: TodoItem) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: TodoItem) {
        todoDao.deleteTodo(todo)

        // Also delete from Microsoft To Do if synced
        if (todo.microsoftTodoId != null && microsoftTodoService.isAuthenticated()) {
            microsoftTodoService.deleteTodoTask(todo.microsoftTodoId)
        }
    }

    suspend fun syncTodosWithMicrosoft() {
        if (!microsoftTodoService.isAuthenticated()) return

        val pendingTodos = todoDao.getPendingSyncTodos()

        for (todo in pendingTodos) {
            try {
                val microsoftId = microsoftTodoService.createTodoTask(todo)
                if (microsoftId != null) {
                    val updatedTodo = todo.copy(
                        microsoftTodoId = microsoftId,
                        syncStatus = SyncStatus.SYNCED
                    )
                    todoDao.updateTodo(updatedTodo)
                } else {
                    todoDao.updateTodo(todo.copy(syncStatus = SyncStatus.FAILED))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                todoDao.updateTodo(todo.copy(syncStatus = SyncStatus.FAILED))
            }
        }
    }

    suspend fun getAccountById(accountId: String): EmailAccount? {
        return emailAccountDao.getAccountById(accountId)
    }
}
