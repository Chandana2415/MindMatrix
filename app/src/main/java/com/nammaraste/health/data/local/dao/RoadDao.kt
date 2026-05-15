package com.nammaraste.health.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nammaraste.health.data.local.entities.Road
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadDao {
    @Query("SELECT * FROM roads ORDER BY healthScore ASC")
    fun getAllRoads(): Flow<List<Road>>

    @Query("SELECT * FROM roads WHERE roadId = :id")
    fun getRoadById(id: Int): Flow<Road?>

    @Query("SELECT * FROM roads WHERE roadName LIKE '%' || :q || '%' OR talukaName LIKE '%' || :q || '%'")
    fun searchRoads(q: String): Flow<List<Road>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roads: List<Road>)

    @Update
    suspend fun updateRoad(road: Road)

    @Query("SELECT AVG(healthScore) FROM roads")
    fun getAvgHealthScore(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM roads")
    fun getTotalRoadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM roads WHERE warrantyEndDate > :now")
    fun getRoadsInWarranty(now: Long): Flow<Int>
}
