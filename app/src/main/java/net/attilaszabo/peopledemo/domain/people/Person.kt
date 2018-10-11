package net.attilaszabo.peopledemo.domain.people

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Person(
    var name: String,
    var region: String,
    var age: Int,
    var photo: String
) : Parcelable
