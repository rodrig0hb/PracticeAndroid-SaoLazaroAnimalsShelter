package br.com.abrigosaolazaro.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    // ── READ ─────────────────────────────────────────────────────────

    /** Todos os animais disponíveis, ordenados por nome. */
    @Query("SELECT * FROM animals WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailableAnimals(): Flow<List<AnimalEntity>>

    /** Animais marcados como favorito. */
    @Query("SELECT * FROM animals WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<AnimalEntity>>

    /** Busca por nome (LIKE). */
    @Query("SELECT * FROM animals WHERE name LIKE '%' || :query || '%' AND isAvailable = 1")
    fun searchByName(query: String): Flow<List<AnimalEntity>>

    /** Contagem total de registros. */
    @Query("SELECT COUNT(*) FROM animals")
    suspend fun count(): Int

    // ── INSERT ────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    // ── UPDATE ────────────────────────────────────────────────────────

    /** Alterna o estado de favorito. */
    @Query("UPDATE animals SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    /** Incrementa o contador de visualizações do card. */
    @Query("UPDATE animals SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Long)

    // ── DELETE ────────────────────────────────────────────────────────

    /** Remove um animal pelo id (ex.: após adoção concluída). */
    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Limpa todos os registros (útil para re-seed). */
    @Query("DELETE FROM animals")
    suspend fun deleteAll()
}
