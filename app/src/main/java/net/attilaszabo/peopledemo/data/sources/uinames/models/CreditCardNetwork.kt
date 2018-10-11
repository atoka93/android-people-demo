package net.attilaszabo.peopledemo.data.sources.uinames.models

import com.google.gson.annotations.SerializedName

class CreditCardNetwork {

    @SerializedName("expiration")
    var expiration: String = ""

    @SerializedName("number")
    var number: String = ""

    @SerializedName("pin")
    var pin: Int = 0

    @SerializedName("security")
    var security: Int = 0
}
