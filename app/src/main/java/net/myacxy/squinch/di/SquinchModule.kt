package net.myacxy.squinch.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import net.myacxy.retrotwitch.v5.RxRetroTwitch
import net.myacxy.squinch.SquinchApplication
import net.myacxy.squinch.base.SimpleViewModelLocator
import net.myacxy.squinch.base.di.ApplicationContext
import net.myacxy.squinch.base.di.PerApplication
import net.myacxy.squinch.helpers.DataHelper
import javax.inject.Named

@Module
internal class SquinchModule {

    @Provides
    @PerApplication
    @ApplicationContext
    fun applicationContext(application: SquinchApplication): Context {
        return application.applicationContext
    }

    @Provides
    @PerApplication
    fun dataHelper(@ApplicationContext context: Context): DataHelper {
        return DataHelper(context)
    }

    @Provides
    @PerApplication
    fun simpleViewModelLocator(
            rxRetroTwitch: RxRetroTwitch,
            dataHelper: DataHelper,
            @Named("debug_log") debugLogSharedPreferences: SharedPreferences
    ): SimpleViewModelLocator {
        return SimpleViewModelLocator(rxRetroTwitch, dataHelper, debugLogSharedPreferences)
    }
}
