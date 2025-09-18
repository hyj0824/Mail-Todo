package com.mailtodo.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "emails")
data class Email(
    @PrimaryKey val uid: String,
    val accountId: String,
    val subject: String,
    val from: String,
    val to: String,
    val content: String,
    val receivedDate: Date,
    val isRead: Boolean = false,
    val hasDateContent: Boolean = false,
    val extractedDates: String? = null, // JSON string of extracted dates
    val summary: String? = null,
    val isProcessed: Boolean = false
)

@Entity(tableName = "email_accounts")
data class EmailAccount(
    @PrimaryKey val id: String,
    val emailAddress: String,
    val accountType: AccountType,
    val imapServer: String,
    val imapPort: Int,
    val username: String,
    val password: String, // Should be encrypted in production
    val isActive: Boolean = true,
    val lastSyncTime: Date? = null
)

enum class AccountType {
    OUTLOOK, QQ, GENERIC
}

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val dueDate: Date?,
    val isCompleted: Boolean = false,
    val emailUid: String?, // Reference to source email
    val microsoftTodoId: String? = null, // ID in Microsoft To Do
    val createdAt: Date = Date(),
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

enum class SyncStatus {
    PENDING, SYNCED, FAILED
}

data class ExtractedDate(
    val dateText: String,
    val parsedDate: Date,
    val context: String // Surrounding text for context
)
