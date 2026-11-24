package com.cancreative.rawggames.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteGame(
    @PrimaryKey val id: Int,
    val name: String,
    val backgroundImage: String?,
    val rating: Double,
    val released: String?,
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY name")
    fun observeAll(): Flow<List<FavoriteGame>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    fun observe(id: Int): Flow<FavoriteGame?>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun exists(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: FavoriteGame)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun delete(id: Int)
}

@Database(entities = [FavoriteGame::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "rawg.db")
                    .build()
                    .also { instance = it }
            }
    }
}
