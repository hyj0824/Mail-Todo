package com.mailtodo.app.data.dao

import androidx.room.*
import com.mailtodo.app.data.model.Email
import com.mailtodo.app.data.model.EmailAccount
import com.mailtodo.app.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails WHERE accountId = :accountId ORDER BY receivedDate DESC")
    fun getEmailsByAccount(accountId: String): Flow<List<Email>>

    @Query("SELECT * FROM emails WHERE hasDateContent = 1 AND isProcessed = 0")
    suspend fun getUnprocessedEmailsWithDates(): List<Email>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<Email>)

    @Update
    suspend fun updateEmail(email: Email)

    @Query("SELECT * FROM emails WHERE uid = :uid")
    suspend fun getEmailByUid(uid: String): Email?
}

@Dao
interface EmailAccountDao {
    @Query("SELECT * FROM email_accounts WHERE isActive = 1")
    fun getActiveAccounts(): Flow<List<EmailAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: EmailAccount)

    @Update
    suspend fun updateAccount(account: EmailAccount)

    @Delete
    suspend fun deleteAccount(account: EmailAccount)

    @Query("SELECT * FROM email_accounts WHERE id = :id")
    suspend fun getAccountById(id: String): EmailAccount?
}

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY dueDate ASC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSyncTodos(): List<TodoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("SELECT * FROM todo_items WHERE emailUid = :emailUid")
    suspend fun getTodosByEmailUid(emailUid: String): List<TodoItem>
}
