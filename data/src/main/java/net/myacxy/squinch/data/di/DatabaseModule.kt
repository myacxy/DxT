package net.myacxy.squinch.data.di

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Debug
import dagger.Module
import dagger.Provides
import net.myacxy.squinch.base.di.ApplicationContext
import net.myacxy.squinch.base.di.PerApplication
import net.myacxy.squinch.data.SquinchDatabase
import net.myacxy.squinch.data.dao.DebugLogDao

@Module
class DatabaseModule {

    @Provides
    @PerApplication
    fun squinchDatabase(@ApplicationContext context: Context): SquinchDatabase {
        val builder = Room.databaseBuilder(context, SquinchDatabase::class.java, "squinch.db")
                .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }

    @Provides
    fun debugLogDao(database: SquinchDatabase): DebugLogDao = database.debugLogDao()
}
