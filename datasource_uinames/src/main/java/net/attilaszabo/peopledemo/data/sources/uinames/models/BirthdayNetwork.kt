package net.attilaszabo.peopledemo.data.sources.uinames.models

import com.google.gson.annotations.SerializedName

class BirthdayNetwork {

    @SerializedName("dmy")
    var dayMonthYear: String = "01/01/1970"

    @SerializedName("mdy")
    var monthDayYear: String = "01/01/1970"

    @SerializedName("raw")
    var raw: Long = 0
}
