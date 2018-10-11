package net.attilaszabo.peopledemo.data.sources.uinames

import io.reactivex.Flowable
import net.attilaszabo.peopledemo.data.sources.uinames.models.PersonNetwork
import retrofit2.http.POST
import retrofit2.http.Query

interface UINamesApiService {

    @POST("?ext")
    fun loadPeople(@Query("amount") amount: Int): Flowable<List<PersonNetwork>>
}
