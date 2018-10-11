package net.attilaszabo.peopledemo.data.sources.local.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.attilaszabo.peopledemo.data.sources.local.database.CacheDatabase

@Entity(tableName = CacheDatabase.PEOPLE_TABLE_NAME)
data class PersonDatabase(

    @PrimaryKey
    var name: String = "",

    @ColumnInfo(name = "region")
    var region: String = "",

    @ColumnInfo(name = "age")
    var age: Int = 0,

    @ColumnInfo(name = "photo")
    var photo: String = ""
)
