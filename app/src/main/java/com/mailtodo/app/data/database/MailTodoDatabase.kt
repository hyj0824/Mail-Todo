package com.mailtodo.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.mailtodo.app.data.dao.EmailDao
import com.mailtodo.app.data.dao.EmailAccountDao
import com.mailtodo.app.data.dao.TodoDao
import com.mailtodo.app.data.model.Email
import com.mailtodo.app.data.model.EmailAccount
import com.mailtodo.app.data.model.TodoItem

@Database(
    entities = [Email::class, EmailAccount::class, TodoItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MailTodoDatabase : RoomDatabase() {
    abstract fun emailDao(): EmailDao
    abstract fun emailAccountDao(): EmailAccountDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: MailTodoDatabase? = null

        fun getDatabase(context: Context): MailTodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MailTodoDatabase::class.java,
                    "mail_todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
