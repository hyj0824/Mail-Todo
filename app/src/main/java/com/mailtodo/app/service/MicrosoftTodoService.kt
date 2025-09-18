package com.mailtodo.app.service

import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.models.TodoTask
import com.microsoft.graph.models.TodoTaskList
import com.microsoft.graph.requests.GraphServiceClient
import com.mailtodo.app.data.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MicrosoftTodoService @Inject constructor() {

    private var graphServiceClient: GraphServiceClient<*>? = null
    private var isAuthenticated = false

    fun initialize(authProvider: IAuthenticationProvider) {
        graphServiceClient = GraphServiceClient.builder()
            .authenticationProvider(authProvider)
            .buildClient()
        isAuthenticated = true
    }

    suspend fun createTodoTask(todoItem: TodoItem): String? = withContext(Dispatchers.IO) {
        if (!isAuthenticated || graphServiceClient == null) {
            return@withContext null
        }

        try {
            val task = TodoTask().apply {
                title = todoItem.title
                body = TodoTask.ItemBody().apply {
                    content = todoItem.description ?: ""
                    contentType = TodoTask.BodyType.TEXT
                }
                if (todoItem.dueDate != null) {
                    dueDateTime = TodoTask.DateTimeTimeZone().apply {
                        dateTime = todoItem.dueDate.toString()
                        timeZone = TimeZone.getDefault().id
                    }
                }
            }

            // 获取默认任务列表
            val taskLists = graphServiceClient!!.me().todo().lists().buildRequest().get()
            val defaultList = taskLists?.currentPage?.firstOrNull()

            if (defaultList != null) {
                val createdTask = graphServiceClient!!.me().todo()
                    .lists(defaultList.id)
                    .tasks()
                    .buildRequest()
                    .post(task)

                return@withContext createdTask?.id
            }

            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun updateTodoTask(microsoftTodoId: String, todoItem: TodoItem): Boolean = withContext(Dispatchers.IO) {
        if (!isAuthenticated || graphServiceClient == null) {
            return@withContext false
        }

        try {
            val task = TodoTask().apply {
                title = todoItem.title
                body = TodoTask.ItemBody().apply {
                    content = todoItem.description ?: ""
                    contentType = TodoTask.BodyType.TEXT
                }
                status = if (todoItem.isCompleted) TodoTask.TaskStatus.COMPLETED else TodoTask.TaskStatus.NOT_STARTED
            }

            // 获取默认任务列表
            val taskLists = graphServiceClient!!.me().todo().lists().buildRequest().get()
            val defaultList = taskLists?.currentPage?.firstOrNull()

            if (defaultList != null) {
                graphServiceClient!!.me().todo()
                    .lists(defaultList.id)
                    .tasks(microsoftTodoId)
                    .buildRequest()
                    .patch(task)

                return@withContext true
            }

            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun deleteTodoTask(microsoftTodoId: String): Boolean = withContext(Dispatchers.IO) {
        if (!isAuthenticated || graphServiceClient == null) {
            return@withContext false
        }

        try {
            val taskLists = graphServiceClient!!.me().todo().lists().buildRequest().get()
            val defaultList = taskLists?.currentPage?.firstOrNull()

            if (defaultList != null) {
                graphServiceClient!!.me().todo()
                    .lists(defaultList.id)
                    .tasks(microsoftTodoId)
                    .buildRequest()
                    .delete()

                return@withContext true
            }

            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun isAuthenticated(): Boolean = isAuthenticated
}
