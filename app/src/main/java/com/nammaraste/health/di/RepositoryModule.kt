package com.nammaraste.health.di

import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.RoadDao
import com.nammaraste.health.data.repository.ReportRepository
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.HealthScoreComputer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRoadRepository(
        roadDao: RoadDao,
        reportDao: DamageReportDao,
        scorer: HealthScoreComputer
    ): RoadRepository {
        return RoadRepository(roadDao, reportDao, scorer)
    }

    @Provides
    @Singleton
    fun provideReportRepository(
        reportDao: DamageReportDao,
        roadDao: RoadDao,
        scorer: HealthScoreComputer
    ): ReportRepository {
        return ReportRepository(reportDao, roadDao, scorer)
    }
}
