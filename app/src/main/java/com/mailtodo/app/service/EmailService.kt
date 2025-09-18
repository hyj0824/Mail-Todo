package com.mailtodo.app.service

import com.mailtodo.app.data.model.Email
import com.mailtodo.app.data.model.EmailAccount
import com.mailtodo.app.data.model.AccountType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.MimeMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailService @Inject constructor() {

    suspend fun fetchEmails(account: EmailAccount, limit: Int = 50): List<Email> = withContext(Dispatchers.IO) {
        try {
            val properties = Properties().apply {
                put("mail.store.protocol", "imaps")
                put("mail.imaps.host", account.imapServer)
                put("mail.imaps.port", account.imapPort.toString())
                put("mail.imaps.ssl.enable", "true")
                put("mail.imaps.ssl.trust", "*")
            }

            val session = Session.getInstance(properties)
            val store = session.getStore("imaps")
            store.connect(account.imapServer, account.username, account.password)

            val folder = store.getFolder("INBOX")
            folder.open(Folder.READ_ONLY)

            val messageCount = folder.messageCount
            val start = maxOf(1, messageCount - limit + 1)
            val messages = folder.getMessages(start, messageCount)

            val emails = messages.map { message ->
                convertToEmail(message as MimeMessage, account.id)
            }.reversed() // Most recent first

            folder.close(false)
            store.close()

            emails
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun convertToEmail(message: MimeMessage, accountId: String): Email {
        val uid = generateUid(message)
        val subject = message.subject ?: ""
        val from = message.from?.firstOrNull()?.toString() ?: ""
        val to = message.allRecipients?.joinToString(", ") { it.toString() } ?: ""
        val content = getTextContent(message)
        val receivedDate = message.receivedDate ?: message.sentDate ?: Date()

        return Email(
            uid = uid,
            accountId = accountId,
            subject = subject,
            from = from,
            to = to,
            content = content,
            receivedDate = receivedDate,
            isRead = message.isSet(Flags.Flag.SEEN),
            hasDateContent = containsDateKeywords(content),
            extractedDates = null,
            summary = null,
            isProcessed = false
        )
    }

    private fun generateUid(message: MimeMessage): String {
        return "${message.messageID ?: UUID.randomUUID().toString()}"
    }

    private fun getTextContent(message: MimeMessage): String {
        return try {
            when {
                message.isMimeType("text/plain") -> {
                    message.content as String
                }
                message.isMimeType("text/html") -> {
                    // Simple HTML to text conversion
                    (message.content as String).replace(Regex("<[^>]+>"), "")
                }
                message.isMimeType("multipart/*") -> {
                    extractTextFromMultipart(message.content as Multipart)
                }
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractTextFromMultipart(multipart: Multipart): String {
        val text = StringBuilder()
        for (i in 0 until multipart.count) {
            val part = multipart.getBodyPart(i)
            when {
                part.isMimeType("text/plain") -> {
                    text.append(part.content as String)
                }
                part.isMimeType("text/html") -> {
                    text.append((part.content as String).replace(Regex("<[^>]+>"), ""))
                }
                part.isMimeType("multipart/*") -> {
                    text.append(extractTextFromMultipart(part.content as Multipart))
                }
            }
        }
        return text.toString()
    }

    private fun containsDateKeywords(content: String): Boolean {
        val dateKeywords = listOf(
            "会议", "meeting", "约", "appointment", "deadline", "截止",
            "提醒", "reminder", "日程", "schedule", "活动", "event",
            "明天", "tomorrow", "下周", "next week", "月", "month",
            "日", "day", "时间", "time", "点", "o'clock"
        )
        val lowerContent = content.lowercase()
        return dateKeywords.any { lowerContent.contains(it) }
    }

    fun getImapConfig(accountType: AccountType): Pair<String, Int> {
        return when (accountType) {
            AccountType.OUTLOOK -> "outlook.office365.com" to 993
            AccountType.QQ -> "imap.qq.com" to 993
            AccountType.GENERIC -> "imap.gmail.com" to 993 // Default fallback
        }
    }
}
