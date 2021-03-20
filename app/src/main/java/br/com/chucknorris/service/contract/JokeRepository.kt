package br.com.chucknorris.service.contract

import br.com.chucknorris.service.model.Joke
import br.com.chucknorris.service.model.JokeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface JokeRepository {
    fun getRandomJokeAsync(category: String?): Deferred<Response<Joke>>

    fun fetchCategoriesAsync(): Deferred<Response<List<String>>>

    fun fetchJokesBySearchAsync(searchText: String): Deferred<Response<JokeResponse>>
}
