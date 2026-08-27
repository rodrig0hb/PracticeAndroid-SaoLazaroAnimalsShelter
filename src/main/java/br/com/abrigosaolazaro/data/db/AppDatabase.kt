package br.com.abrigosaolazaro.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AnimalEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "abrigo_saolazaro_v2.db"
                ).build().also { INSTANCE = it }
            }
    }
}
