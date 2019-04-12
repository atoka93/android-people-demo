package net.attilaszabo.peopledemo.data.sources.cachedb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.attilaszabo.peopledemo.data.sources.cachedb.models.PersonDatabase

@Dao
interface PeopleDao {

    @Query("SELECT * FROM ${CacheDatabase.PEOPLE_TABLE_NAME} LIMIT :amount OFFSET :startingAt")
    suspend fun getPeople(startingAt: Int, amount: Int): List<PersonDatabase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(people: List<PersonDatabase>)

    @Query("DELETE FROM ${CacheDatabase.PEOPLE_TABLE_NAME}")
    suspend fun deleteAll()
}
