package com.nammaraste.health.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nammaraste.health.data.local.AppDatabase
import com.nammaraste.health.data.local.SeedData
import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.MaintenanceLogDao
import com.nammaraste.health.data.local.dao.RoadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        provider: Provider<AppDatabase>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "namma_raste_health.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Use a dedicated scope for seeding
                CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    SeedData.populate(provider.get())
                }
            }
        }).build()
    }

    @Provides
    fun provideRoadDao(db: AppDatabase): RoadDao = db.roadDao()

    @Provides
    fun provideDamageReportDao(db: AppDatabase): DamageReportDao = db.reportDao()

    @Provides
    fun provideMaintenanceLogDao(db: AppDatabase): MaintenanceLogDao = db.logDao()
}
