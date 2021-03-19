package br.com.chucknorris.repository

import br.com.chucknorris.repository.api.JokeApiClient
import br.com.chucknorris.repository.model.Joke
import kotlinx.coroutines.Deferred
import retrofit2.Response

class JokeRepository(
    private val jokeApiClient: JokeApiClient
) {
    fun getRandomJokeAsync(category: String? = null): Deferred<Response<Joke>> {
        val params = HashMap<String, String>()

        if (!category.isNullOrEmpty()) {
            params["category"] = category
        }

        return jokeApiClient.getRandomJokeAsync(params)
    }

    fun fetchCategoriesAsync(): Deferred<Response<List<String>>> {
        return jokeApiClient.fetchCategoriesAsync()
    }

    fun fetchJokesBySearchAsync(searchText: String): Deferred<Response<List<Joke>>> {
        val params = HashMap<String, String>()
        params["query"] = searchText

        return jokeApiClient.fetchJokesBySearchAsync(params)
    }
}
