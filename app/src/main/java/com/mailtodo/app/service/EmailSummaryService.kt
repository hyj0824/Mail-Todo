package com.mailtodo.app.service

import com.mailtodo.app.data.model.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailSummaryService @Inject constructor() {

    suspend fun summarizeEmail(email: Email): String = withContext(Dispatchers.Default) {
        val content = email.content
        val subject = email.subject

        // 简单的摘要算法，提取关键信息
        val summary = buildString {
            // 添加主题
            append("主题: $subject\n")

            // 提取关键句子
            val keyPhrases = extractKeyPhrases(content)
            if (keyPhrases.isNotEmpty()) {
                append("关键内容: ")
                append(keyPhrases.joinToString("; "))
                append("\n")
            }

            // 提取时间相关信息
            val timeInfo = extractTimeRelatedInfo(content)
            if (timeInfo.isNotEmpty()) {
                append("时间信息: ")
                append(timeInfo.joinToString("; "))
            }
        }

        return@withContext if (summary.length > 200) {
            summary.take(197) + "..."
        } else {
            summary
        }
    }

    private fun extractKeyPhrases(content: String): List<String> {
        val keywordPatterns = mapOf(
            "会议" to listOf("会议", "meeting", "开会", "讨论"),
            "任务" to listOf("任务", "task", "工作", "完成", "deadline"),
            "提醒" to listOf("提醒", "reminder", "记住", "别忘了"),
            "活动" to listOf("活动", "event", "聚会", "聚餐"),
            "约会" to listOf("约", "见面", "appointment", "拜访")
        )

        val sentences = content.split(Regex("[。！？.!?]"))
        val keyPhrases = mutableListOf<String>()

        for (sentence in sentences) {
            if (sentence.length < 10 || sentence.length > 100) continue

            for ((category, keywords) in keywordPatterns) {
                if (keywords.any { sentence.contains(it, ignoreCase = true) }) {
                    keyPhrases.add(sentence.trim())
                    break
                }
            }
        }

        return keyPhrases.take(3) // 最多3个关键短语
    }

    private fun extractTimeRelatedInfo(content: String): List<String> {
        val timePatterns = listOf(
            Regex("\\d{1,2}月\\d{1,2}日"),
            Regex("\\d{4}年\\d{1,2}月\\d{1,2}日"),
            Regex("明天|后天|下周|下月"),
            Regex("\\d{1,2}点\\d{0,2}分?"),
            Regex("上午|下午|晚上\\d{1,2}点"),
            Regex("周[一二三四五六日]")
        )

        val timeInfo = mutableListOf<String>()

        for (pattern in timePatterns) {
            val matches = pattern.findAll(content)
            for (match in matches) {
                val context = extractContext(content, match.value)
                if (context.isNotBlank()) {
                    timeInfo.add(context)
                }
            }
        }

        return timeInfo.distinct().take(2) // 最多2个时间信息
    }

    private fun extractContext(text: String, match: String): String {
        val index = text.indexOf(match)
        if (index == -1) return ""

        val start = maxOf(0, index - 20)
        val end = minOf(text.length, index + match.length + 20)

        return text.substring(start, end).trim()
    }
}
