package net.attilaszabo.peopledemo.data.sources.uinames.models

import com.google.gson.annotations.SerializedName

class PersonNetwork {

    @SerializedName("name")
    var name: String = ""

    @SerializedName("surname")
    var surname: String = ""

    @SerializedName("gender")
    var gender: String = ""

    @SerializedName("region")
    var region: String = ""

    @SerializedName("age")
    var age: Int = 0

    @SerializedName("title")
    var title: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("birthday")
    var birthday: BirthdayNetwork = BirthdayNetwork()

    @SerializedName("email")
    var email: String = ""

    @SerializedName("password")
    var password: String = ""

    @SerializedName("credit_card")
    var creditCard: CreditCardNetwork = CreditCardNetwork()

    @SerializedName("photo")
    var photo: String = ""
}
