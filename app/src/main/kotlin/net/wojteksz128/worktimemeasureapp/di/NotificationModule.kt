package net.wojteksz128.worktimemeasureapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.wojteksz128.worktimemeasureapp.notification.NotificationActionImpl
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.EndOfWorkActionImpl
import net.wojteksz128.worktimemeasureapp.notification.worktime.action.IgnoreReminderActionImpl
import net.wojteksz128.worktimemeasureapp.util.comeevent.ComeEventUtils
import javax.inject.Named
import javax.inject.Singleton

const val endOfWorkActionName = "EndOfWorkAction"
const val ignoreReminderActionName = "IgnoreReminderAction"

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    @Named(endOfWorkActionName)
    fun provideEndOfWorkAction(comeEventUtils: ComeEventUtils): NotificationActionImpl =
        EndOfWorkActionImpl(comeEventUtils)

    @Singleton
    @Provides
    @Named(ignoreReminderActionName)
    fun provideIgnoreReminderAction(): NotificationActionImpl = IgnoreReminderActionImpl
}