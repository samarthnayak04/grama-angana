package com.example.gramaangana

import androidx.room.*

@Entity(tableName = "maintenance_items")
data class MaintenanceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val raised: Int,
    val target: Int
)

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_items")
    fun getAll(): List<MaintenanceItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: MaintenanceItem)
}

@Database(entities = [MaintenanceItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "grama_db").build().also { INSTANCE = it }
            }
        }
    }
}