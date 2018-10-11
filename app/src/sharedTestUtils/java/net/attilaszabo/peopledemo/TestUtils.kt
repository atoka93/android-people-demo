package net.attilaszabo.peopledemo

import net.attilaszabo.peopledemo.data.sources.local.database.models.PersonDatabase
import net.attilaszabo.peopledemo.data.sources.uinames.models.PersonNetwork
import net.attilaszabo.peopledemo.domain.people.Person
import java.util.Random

object TestUtils {

    fun generatePerson(): Person =
        Person(
            name = generateFullName(),
            region = generateRegion(),
            age = generateAge(),
            photo = "https://uinames.com/api/photos/male/1.jpg"
        )

    fun generateDatabasePerson(): PersonDatabase =
        PersonDatabase(
            name = generateFullName(),
            region = generateRegion(),
            age = generateAge(),
            photo = "https://uinames.com/api/photos/male/1.jpg"
        )

    fun generateNetworkPerson(): PersonNetwork =
        PersonNetwork().apply {
            name = generateShortName()
            surname = generateShortName()
            region = generateRegion()
            age = generateAge()
            photo = "https://uinames.com/api/photos/male/1.jpg"
        }

    private fun generateString(length: Long): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "
        return Random().ints(length, 0, letters.length)
            .mapToObj { letters[it].toString() }
            .toArray()
            .joinToString(separator = "")
    }

    private fun generateFullName(): String = "${generateShortName()} ${generateShortName()}"

    private fun generateShortName(): String = generateString(9)

    private fun generateRegion(): String = generateString(12)

    private fun generateAge(): Int = Random().nextInt(100) + 1
}
