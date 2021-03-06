package br.com.chucknorris.feature.joke.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import br.com.chucknorris.global.command.CommandProvider
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.global.events.SingleLiveEvent
import br.com.chucknorris.service.contract.JokeRepository
import br.com.chucknorris.service.model.Joke
import br.com.chucknorris.service.model.JokeResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class JokeListViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var jokeRepositoryMock: JokeRepository
    private lateinit var commandProviderMock: CommandProvider

    private lateinit var viewStateObserverMock: Observer<JokeListViewModel.ViewState>
    private lateinit var commandMock: SingleLiveEvent<GenericCommand>

    private lateinit var viewModel: JokeListViewModel

    @Before
    fun setUp() {
        jokeRepositoryMock = mock()
        commandProviderMock = mock()

        viewStateObserverMock = mock()
        commandMock = mock()

        whenever(commandProviderMock.getCommand())
            .thenReturn(commandMock)

        viewModel = JokeListViewModel(
            jokeRepositoryMock,
            Dispatchers.Unconfined,
            commandProviderMock
        )

        viewModel.viewState.observeForever(viewStateObserverMock)
    }

    @Test
    fun `Fetch fact list by text search, when fetching facts from repository happens successfully, then update fact list`() {

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

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeListViewModel.Command.ShowJokeList(expectedJokeResponse.result)
            )
    }

    @Test
    fun `Fetch fact list by text search, when fetching facts from repository happens successfully, then show loading animation before displaying facts`() {

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

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeListViewModel.ViewState(isFetchingJoke = true)
                )

            verify(commandMock)
                .postValue(
                    JokeListViewModel.Command.ShowJokeList(expectedJokeResponse.result)
                )
        }
    }

    @Test
    fun `Fetch fact list by text search, when fetching facts from repository happens successfully, then hide loading animation after displaying facts`() {

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

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeListViewModel.ViewState(isFetchingJoke = false)
                )

            verify(commandMock)
                .postValue(
                    JokeListViewModel.Command.ShowJokeList(expectedJokeResponse.result)
                )
        }
    }

    @Test
    fun `Fetch fact list by text search, when no facts are found, then show empty state message`() {

        // ARRANGE
        val expectedJokeResponse = JokeResponse(
            2,
            emptyList()
        )
        val textToSearch = "text"

        val expectedResponse = Response.success(expectedJokeResponse)

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeListViewModel.Command.ShowEmptyList
            )
    }

    @Test
    fun `Fetch fact list by text search, when fetching facts from repository fails, then hide loading animation`() {

        // ARRANGE
        val errorCode = 500
        val textToSearch = "text"

        val expectedJsonError = JsonObject().apply {
            addProperty("status", "")
            addProperty("message", "")
        }

        val expectedResponse = Response.error<JokeResponse>(
            errorCode,
            Gson().toJson(expectedJsonError).toResponseBody()
        )
        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        /* this is needed in order to clear the initial notification
         * that this observer receives with `isFetchingJoke` equals to `false`
         * that happens when the `JokeListViewModel` is first created
         */
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        verify(viewStateObserverMock)
            .onChanged(
                JokeListViewModel.ViewState(isFetchingJoke = false)
            )
    }

    @Test
    fun `Fetch fact list by text search, when fetching facts from repository fails, then show fetch error message`() {

        // ARRANGE
        val errorCode = 500
        val errorMessage = "custom message"
        val textToSearch = "text"

        val expectedJsonError = JsonObject().apply {
            addProperty("status", "")
            addProperty("message", errorMessage)
        }

        val expectedResponse = Response.error<JokeResponse>(
            errorCode,
            Gson().toJson(expectedJsonError).toResponseBody()
        )

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeListViewModel.Command.ShowGenericErrorMessage
            )
    }

    @Test
    fun `Fetch fact list by text search, when an exception is thrown from repository, then show fetch error message and hide loading animation`() {

        // ARRANGE
        val textToSearch = "text"

        whenever(jokeRepositoryMock.fetchJokesBySearchAsync(textToSearch))
            .thenAnswer { throw Throwable() }
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.fetchJokesBySearch(textToSearch)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeListViewModel.Command.ShowGenericErrorMessage
            )
        verify(viewStateObserverMock)
            .onChanged(
                JokeListViewModel.ViewState(isFetchingJoke = false)
            )
    }
}
