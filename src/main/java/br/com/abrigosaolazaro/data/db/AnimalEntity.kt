package br.com.abrigosaolazaro.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: String,
    val species: String,
    val breed: String,
    val imageUrl: String,
    val description: String,
    val isAvailable: Boolean = true
)
