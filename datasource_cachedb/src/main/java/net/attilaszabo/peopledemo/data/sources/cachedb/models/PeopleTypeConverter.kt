package net.attilaszabo.peopledemo.data.sources.cachedb.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class PeopleTypeConverter : Serializable {

    @TypeConverter
    fun fromPeopleList(strings: List<PersonDatabase>?): String? {
        if (strings == null) {
            return null
        }
        val type = object : TypeToken<List<PersonDatabase>>() {}.type
        return Gson().toJson(strings, type)
    }

    @TypeConverter
    fun toPeopleList(personString: String?): List<PersonDatabase>? {
        if (personString == null) {
            return null
        }
        val type = object : TypeToken<List<PersonDatabase>>() {}.type
        return Gson().fromJson(personString, type)
    }
}
