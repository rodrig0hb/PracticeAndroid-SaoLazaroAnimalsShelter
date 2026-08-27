package br.com.abrigosaolazaro.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa um animal disponível para adoção.
 * Campos básicos + metadados de UX (visto, favorito).
 */
@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: String,
    val species: String,          // "Cachorro" | "Gato"
    val breed: String,
    val imageUrl: String,
    val description: String,
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val viewCount: Int = 0        // quantas vezes o card foi expandido
)
