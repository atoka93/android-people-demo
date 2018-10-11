package net.attilaszabo.peopledemo.data.sources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.attilaszabo.peopledemo.data.sources.local.database.models.PeopleTypeConverter
import net.attilaszabo.peopledemo.data.sources.local.database.models.PersonDatabase

@Database(entities = [(PersonDatabase::class)], version = 1)
@TypeConverters(PeopleTypeConverter::class)
abstract class CacheDatabase : RoomDatabase() {

    abstract fun peopleDao(): PeopleDao

    companion object {

        const val DATABASE_NAME: String = "cache_database"
        const val PEOPLE_TABLE_NAME: String = "people_table"

        @Volatile
        private var instance: CacheDatabase? = null

        fun getDatabase(context: Context): CacheDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): CacheDatabase =
            Room.databaseBuilder(context, CacheDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}
