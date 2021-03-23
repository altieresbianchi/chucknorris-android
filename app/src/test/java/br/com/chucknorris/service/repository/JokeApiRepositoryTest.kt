package br.com.chucknorris.service.repository

import br.com.chucknorris.service.api.JokeApiClient
import br.com.chucknorris.service.contract.JokeRepository
import br.com.chucknorris.service.model.Joke
import br.com.chucknorris.service.model.JokeResponse
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class JokeApiRepositoryTest {

    private lateinit var jokeApiClientMock: JokeApiClient
    private lateinit var jokeRepositoryMock: JokeRepository

    @Before
    fun setUp() {

        jokeApiClientMock = mock()
        jokeRepositoryMock = JokeApiRepository(jokeApiClientMock)
    }

    @Test
    fun `Get random joke, when it is requested, then return deferred operation status`() {

        // ARRANGE

        val expectedJoke = Joke(
            "a1a1a1",
            "Chuck Norris Fact 1",
            "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
        )
        val category = null

        val expectedResponse = Response.success(expectedJoke)

        val expectedDeferredResponse = CompletableDeferred(expectedResponse)

        whenever(jokeRepositoryMock.getRandomJokeAsync(category)).thenReturn(
            expectedDeferredResponse
        )

        // ACT

        val dataResponse = runBlocking { jokeRepositoryMock.getRandomJokeAsync(category).await() }

        // ASSERT

        Assert.assertEquals(expectedResponse, dataResponse)
    }

    @Test
    fun `Get category list, when it is requested, then return deferred operation status`() {

        // ARRANGE

        val expectedList = listOf("animal", "career", "celebrity", "dev")

        val expectedResponse = Response.success(expectedList)

        val expectedDeferredResponse = CompletableDeferred(expectedResponse)

        whenever(jokeRepositoryMock.fetchCategoriesAsync()).thenReturn(expectedDeferredResponse)

        // ACT

        val dataResponse = runBlocking { jokeRepositoryMock.fetchCategoriesAsync().await() }

        // ASSERT

        Assert.assertEquals(expectedResponse, dataResponse)
    }

    @Test
    fun `Get fact list by text search, when it is requested, then return deferred operation status`() {

        // ARRANGE
        val expectedJokeResponse = JokeResponse(
            2,
            listOf(
                Joke(
                    "a1a1a1",
                    "Chuck Norris Fact 1",
                    "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
                ),
                Joke(
                    "b2b2b2",
                    "Chuck Norris Fact 2",
                    "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
                ),
            )
        )
        val textToSearch = "text"

        val expectedResponse = Response.success(expectedJokeResponse)

        val expectedDeferredResponse = CompletableDeferred(expectedResponse)

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch)).thenReturn(
            expectedDeferredResponse
        )

        // ACT

        val dataResponse =
            runBlocking { jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch).await() }

        // ASSERT

        Assert.assertEquals(expectedResponse, dataResponse)
    }
}
