package br.com.chucknorris.service.repository

import br.com.chucknorris.service.api.JokeApiClient
import br.com.chucknorris.service.contract.JokeRepository
import br.com.chucknorris.service.model.Joke
import br.com.chucknorris.service.model.JokeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response

class JokeApiRepository(
    private val jokeApiClient: JokeApiClient
) : JokeRepository {
    override fun getRandomJokeAsync(category: String?): Deferred<Response<Joke>> {
        val params = HashMap<String, String>()

        if (!category.isNullOrEmpty()) {
            params["category"] = category
        }

        return jokeApiClient.getRandomJokeAsync(params)
    }

    override fun fetchCategoriesAsync(): Deferred<Response<List<String>>> {
        return jokeApiClient.fetchCategoriesAsync()
    }

    override fun fetchJokesBySearchAsync(searchText: String): Deferred<Response<JokeResponse>> {
        val params = HashMap<String, String>()
        params["query"] = searchText

        return jokeApiClient.fetchJokesBySearchAsync(params)
    }
}
