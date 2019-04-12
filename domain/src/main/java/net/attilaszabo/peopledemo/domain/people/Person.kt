package net.attilaszabo.peopledemo.domain.people

import java.io.Serializable

data class Person(
    var name: String,
    var region: String,
    var age: Int,
    var photoUrl: String
) : Serializable
