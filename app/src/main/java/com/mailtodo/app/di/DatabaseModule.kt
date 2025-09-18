package com.mailtodo.app.di

import android.content.Context
import androidx.room.Room
import com.mailtodo.app.data.database.MailTodoDatabase
import com.mailtodo.app.data.dao.EmailDao
import com.mailtodo.app.data.dao.EmailAccountDao
import com.mailtodo.app.data.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MailTodoDatabase {
        return Room.databaseBuilder(
            context,
            MailTodoDatabase::class.java,
            "mail_todo_database"
        ).build()
    }

    @Provides
    fun provideEmailDao(database: MailTodoDatabase): EmailDao = database.emailDao()

    @Provides
    fun provideEmailAccountDao(database: MailTodoDatabase): EmailAccountDao = database.emailAccountDao()

    @Provides
    fun provideTodoDao(database: MailTodoDatabase): TodoDao = database.todoDao()
}

