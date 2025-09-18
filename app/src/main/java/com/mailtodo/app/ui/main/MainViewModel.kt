package com.mailtodo.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mailtodo.app.data.model.Email
import com.mailtodo.app.data.model.TodoItem
import com.mailtodo.app.repository.MailTodoRepository
import com.mailtodo.app.worker.EmailSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MailTodoRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _syncStatus = MutableLiveData<String>()
    val syncStatus: LiveData<String> = _syncStatus

    // Combine emails from all accounts
    val emails: LiveData<List<Email>> = repository.getActiveAccounts()
        .combine(repository.getActiveAccounts()) { accounts, _ ->
            val allEmails = mutableListOf<Email>()
            accounts.forEach { account ->
                repository.getEmailsByAccount(account.id).asLiveData().value?.let { emails ->
                    allEmails.addAll(emails)
                }
            }
            allEmails.sortedByDescending { it.receivedDate }
        }.asLiveData()

    val todos: LiveData<List<TodoItem>> = repository.getAllTodos().asLiveData()

    init {
        _syncStatus.value = "就绪"
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _syncStatus.value = "正在加载..."

                // Process emails for date extraction
                repository.processEmailsForDates()

                _syncStatus.value = "加载完成"
            } catch (e: Exception) {
                _syncStatus.value = "加载失败: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshEmails() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _syncStatus.value = "正在同步邮件..."

                val accounts = repository.getActiveAccounts().asLiveData().value ?: emptyList()

                for (account in accounts) {
                    repository.syncEmails(account.id)
                }

                // Process new emails for dates
                repository.processEmailsForDates()

                _syncStatus.value = "邮件同步完成"
            } catch (e: Exception) {
                _syncStatus.value = "同步失败: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTodoFromEmail(email: Email) {
        viewModelScope.launch {
            try {
                // Parse extracted dates if available
                val dueDate = email.extractedDates?.let { dateStr ->
                    val parts = dateStr.split("|")
                    if (parts.size >= 2) {
                        Date(parts[1].toLong())
                    } else null
                }

                val todo = TodoItem(
                    id = UUID.randomUUID().toString(),
                    title = email.subject,
                    description = email.summary ?: email.content.take(200),
                    dueDate = dueDate,
                    emailUid = email.uid,
                    createdAt = Date()
                )

                repository.addTodo(todo)
                _syncStatus.value = "已创建待办事项"
            } catch (e: Exception) {
                _syncStatus.value = "创建待办事项失败: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun toggleTodoCompletion(todo: TodoItem) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
                repository.updateTodo(updatedTodo)
                _syncStatus.value = if (updatedTodo.isCompleted) "已完成" else "已取消完成"
            } catch (e: Exception) {
                _syncStatus.value = "更新失败: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                _syncStatus.value = "已删除待办事项"
            } catch (e: Exception) {
                _syncStatus.value = "删除失败: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun syncTodosWithMicrosoft() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _syncStatus.value = "正在同步到Microsoft To Do..."

                repository.syncTodosWithMicrosoft()

                _syncStatus.value = "Microsoft To Do同步完成"
            } catch (e: Exception) {
                _syncStatus.value = "Microsoft同步失败: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startBackgroundSync() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<EmailSyncWorker>().build()
        workManager.enqueue(syncWorkRequest)
    }
}
