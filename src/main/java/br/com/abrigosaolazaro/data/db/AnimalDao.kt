package br.com.abrigosaolazaro.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailableAnimals(): Flow<List<AnimalEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int
}
