package net.attilaszabo.peopledemo.domain

import net.attilaszabo.peopledemo.domain.people.Person
import java.util.Random

object DomainTestUtils {

    fun generatePerson(): Person =
        Person(
            name = generateFullName(),
            region = generateRegion(),
            age = generateAge(),
            photoUrl = "https://uinames.com/api/photos/male/1.jpg"
        )

    fun generateString(length: Long): String {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "
        return Random().ints(length, 0, letters.length)
            .mapToObj { letters[it].toString() }
            .toArray()
            .joinToString(separator = "")
    }

    fun generateFullName(): String = "${generateShortName()} ${generateShortName()}"

    fun generateShortName(): String = generateString(9)

    fun generateRegion(): String = generateString(12)

    fun generateAge(): Int = Random().nextInt(100) + 1
}
