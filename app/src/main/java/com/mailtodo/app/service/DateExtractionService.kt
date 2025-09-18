package com.mailtodo.app.service

import com.joestelmach.natty.DateGroup
import com.joestelmach.natty.Parser
import com.mailtodo.app.data.model.ExtractedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateExtractionService @Inject constructor() {

    private val parser = Parser()

    // 中文日期模式
    private val chineseDatePatterns = listOf(
        Pattern.compile("(\\d{1,2})月(\\d{1,2})日"),
        Pattern.compile("(明天|后天|下周|下个月)"),
        Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日"),
        Pattern.compile("(\\d{1,2})点(\\d{1,2})分"),
        Pattern.compile("(上午|下午|晚上)(\\d{1,2})点"),
        Pattern.compile("周(一|二|三|四|五|六|日)"),
        Pattern.compile("(\\d{1,2})号")
    )

    suspend fun extractDatesFromText(text: String): List<ExtractedDate> = withContext(Dispatchers.Default) {
        val extractedDates = mutableListOf<ExtractedDate>()

        // 使用 Natty 解析英文日期
        try {
            val groups: List<DateGroup> = parser.parse(text)
            for (group in groups) {
                for (date in group.dates) {
                    val matchText = group.text
                    val context = extractContext(text, matchText)
                    extractedDates.add(
                        ExtractedDate(
                            dateText = matchText,
                            parsedDate = date,
                            context = context
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 解析中文日期
        extractedDates.addAll(extractChineseDates(text))

        extractedDates.distinctBy { it.dateText }
    }

    private fun extractChineseDates(text: String): List<ExtractedDate> {
        val extractedDates = mutableListOf<ExtractedDate>()
        val calendar = Calendar.getInstance()

        chineseDatePatterns.forEach { pattern ->
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val matchText = matcher.group()
                val context = extractContext(text, matchText)
                val parsedDate = parseChineseDate(matchText, calendar)

                if (parsedDate != null) {
                    extractedDates.add(
                        ExtractedDate(
                            dateText = matchText,
                            parsedDate = parsedDate,
                            context = context
                        )
                    )
                }
            }
        }

        return extractedDates
    }

    private fun parseChineseDate(dateText: String, baseCalendar: Calendar): Date? {
        val calendar = Calendar.getInstance()
        calendar.time = baseCalendar.time

        return when {
            dateText.contains("明天") -> {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.time
            }
            dateText.contains("后天") -> {
                calendar.add(Calendar.DAY_OF_MONTH, 2)
                calendar.time
            }
            dateText.contains("下周") -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.time
            }
            dateText.contains("下个月") -> {
                calendar.add(Calendar.MONTH, 1)
                calendar.time
            }
            dateText.matches(Regex("\\d{1,2}月\\d{1,2}日")) -> {
                val parts = dateText.replace("月", "-").replace("日", "").split("-")
                if (parts.size == 2) {
                    calendar.set(Calendar.MONTH, parts[0].toInt() - 1)
                    calendar.set(Calendar.DAY_OF_MONTH, parts[1].toInt())
                    calendar.time
                } else null
            }
            else -> null
        }
    }

    private fun extractContext(text: String, matchText: String): String {
        val index = text.indexOf(matchText)
        if (index == -1) return ""

        val start = maxOf(0, index - 30)
        val end = minOf(text.length, index + matchText.length + 30)

        return text.substring(start, end).trim()
    }
}
