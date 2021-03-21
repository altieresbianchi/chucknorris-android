package br.com.chucknorris.service.api

import br.com.chucknorris.service.model.Joke
import br.com.chucknorris.service.model.JokeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface JokeApiClient {
    @GET("jokes/random")
    fun getRandomJokeAsync(
        @QueryMap params: Map<String, String>
    ): Deferred<Response<Joke>>

    @GET("jokes/categories")
    fun fetchCategoriesAsync(): Deferred<Response<List<String>>>

    @GET("jokes/search")
    fun fetchJokesBySearchAsync(
        @QueryMap params: Map<String, String>
    ): Deferred<Response<JokeResponse>>
}
