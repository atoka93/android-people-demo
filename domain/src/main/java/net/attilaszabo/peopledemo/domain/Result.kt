package net.attilaszabo.peopledemo.domain

sealed class Result<out TModel> {

    class Success<out TModel>(val result: TModel) : Result<TModel>()

    class Error<out TModel>(val throwable: Throwable?) : Result<TModel>()
}
