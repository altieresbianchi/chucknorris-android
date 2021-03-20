package br.com.chucknorris.repository.api

import br.com.chucknorris.repository.model.Joke
import br.com.chucknorris.repository.model.JokeResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.*

interface JokeApiClient {

    // GET https://api.chucknorris.io/jokes/random
    /*Example response:
    {
        "icon_url" : "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
        "id" : "QgqdNQMGSPSbao9PZrf5Kw",
        "url" : "",
        "value" : "Chuck Norris lost both his legs in a car accident....and still managed to walk it off"
    }*/

    // Retrieve a random chuck norris joke from a given category.
    // GET https://api.chucknorris.io/jokes/random?category={category}

    //Retrieve a list of available categories.
    // GET https://api.chucknorris.io/jokes/categories

    // Free text search.
    // GET https://api.chucknorris.io/jokes/search?query={query}

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

    /*@Headers("Accept: application/json", "Content-Type: application/json")
    @POST("login")
    fun login(
        @Body body: TreeMap<String, String>
    ): Deferred<Response<Any>>

    @POST("auth")
    fun authWithCode(
        @Body body: TreeMap<String, String>
    ): Deferred<Response<UserLTMInfos>>

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    @POST("login/password-recovery")
    fun recoverPassword(
        @Body body: TreeMap<String, String>
    ): Deferred<Response<Void>>*/
}
