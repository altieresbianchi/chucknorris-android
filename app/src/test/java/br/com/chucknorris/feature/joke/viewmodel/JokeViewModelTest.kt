package br.com.chucknorris.feature.joke.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import br.com.chucknorris.global.command.CommandProvider
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.global.events.SingleLiveEvent
import br.com.chucknorris.service.contract.JokeRepository
import br.com.chucknorris.service.model.Joke
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

class JokeViewModelTest {
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var jokeRepositoryMock: JokeRepository
    private lateinit var commandProviderMock: CommandProvider

    private lateinit var viewStateObserverMock: Observer<JokeViewModel.ViewState>
    private lateinit var commandMock: SingleLiveEvent<GenericCommand>

    private lateinit var viewModel: JokeViewModel

    @Before
    fun setUp() {
        jokeRepositoryMock = mock()
        commandProviderMock = mock()

        viewStateObserverMock = mock()
        commandMock = mock()

        whenever(commandProviderMock.getCommand())
            .thenReturn(commandMock)

        viewModel = JokeViewModel(
            jokeRepositoryMock,
            Dispatchers.Unconfined,
            commandProviderMock
        )

        viewModel.viewState.observeForever(viewStateObserverMock)
    }

    @Test
    fun `Fetch category list, when fetching categories from repository happens successfully, then update category list`() {

        // ARRANGE
        val expectedList = listOf("animal", "career", "celebrity", "dev")

        val expectedResponse = Response.success(expectedList)

        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowCategoryList(expectedList)
            )
    }

    @Test
    fun `Fetch category list, when fetching categories from repository happens successfully, then show loading animation before displaying categories`() {

        // ARRANGE
        val expectedList = listOf("animal", "career", "celebrity", "dev")

        val expectedResponse = Response.success(expectedList)

        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeViewModel.ViewState(isFetchingCategories = true)
                )

            verify(commandMock)
                .postValue(
                    JokeViewModel.Command.ShowCategoryList(expectedList)
                )
        }
    }

    @Test
    fun `Fetch category list, when fetching categories from repository happens successfully, then hide loading animation after displaying categories`() {

        // ARRANGE
        val expectedList = listOf("animal", "career", "celebrity", "dev")

        val expectedResponse = Response.success(expectedList)

        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeViewModel.ViewState(isFetchingCategories = false)
                )

            verify(commandMock)
                .postValue(
                    JokeViewModel.Command.ShowCategoryList(expectedList)
                )
        }
    }

    @Test
    fun `Fetch category list, when no categories are found, then show empty state message`() {

        // ARRANGE
        val expectedList = emptyList<String>()

        val expectedResponse = Response.success(expectedList)

        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowCategoryList(expectedList)
            )
    }


    @Test
    fun `Fetch category list, when fetching categories from repository fails, then hide loading animation`() {

        // ARRANGE
        val errorCode = 500

        val expectedJsonError = JsonObject().apply {
            addProperty("status", "")
            addProperty("message", "")
        }

        val expectedResponse = Response.error<List<String>>(
            errorCode,
            Gson().toJson(expectedJsonError).toResponseBody()
        )
        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenReturn(CompletableDeferred(expectedResponse))

        /* this is needed in order to clear the initial notification
         * that this observer receives with `isFetchingJoke` equals to `false`
         * that happens when the `JokeViewModel` is first created
         */
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        verify(viewStateObserverMock)
            .onChanged(
                JokeViewModel.ViewState(isFetchingCategories = false)
            )
    }

    @Test
    fun `Fetch category list, when an exception is thrown from repository, then show fetch error message and hide loading animation`() {

        // ARRANGE

        whenever(jokeRepositoryMock.fetchCategoriesAsync())
            .thenAnswer { throw Throwable() }
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.fetchCategories()

        // ASSERT
        verify(viewStateObserverMock)
            .onChanged(
                JokeViewModel.ViewState(isFetchingCategories = false)
            )
    }

    @Test
    fun `Fetch random fact with without category, when fetching fact from repository happens successfully, then update fact data`() {

        // ARRANGE
        val expectedJoke = Joke(
            "a1a1a1",
            "Chuck Norris Fact 1",
            "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
        )
        val category = null

        val expectedResponse = Response.success(expectedJoke)

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowJokeCard(expectedJoke)
            )
    }

    @Test
    fun `Fetch random fact by filtering by category, when fetching fact from repository happens successfully, then update fact data`() {

        // ARRANGE
        val expectedJoke = Joke(
            "a1a1a1",
            "Chuck Norris Fact 1",
            "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
        )
        val category = "animal"

        val expectedResponse = Response.success(expectedJoke)

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowJokeCard(expectedJoke)
            )
    }

    @Test
    fun `Fetch random fact, when fetching fact from repository happens successfully, then show loading animation before displaying fact`() {

        // ARRANGE
        val expectedJoke = Joke(
            "a1a1a1",
            "Chuck Norris Fact 1",
            "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
        )
        val category = null

        val expectedResponse = Response.success(expectedJoke)

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeViewModel.ViewState(isFetchingJoke = true)
                )

            verify(commandMock)
                .postValue(
                    JokeViewModel.Command.ShowJokeCard(expectedJoke)
                )
        }
    }

    @Test
    fun `Fetch random fact, when fetching fact from repository happens successfully, then hide loading animation after displaying fact`() {

        // ARRANGE
        val expectedJoke = Joke(
            "a1a1a1",
            "Chuck Norris Fact 1",
            "https://assets.chucknorris.host/img/avatar/chuck-norris.png"
        )
        val category = null

        val expectedResponse = Response.success(expectedJoke)

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        inOrder(commandMock, viewStateObserverMock) {
            verify(viewStateObserverMock)
                .onChanged(
                    JokeViewModel.ViewState(isFetchingJoke = false)
                )

            verify(commandMock)
                .postValue(
                    JokeViewModel.Command.ShowJokeCard(expectedJoke)
                )
        }
    }

    @Test
    fun `Fetch random fact, when fetching fact from repository fails, then hide loading animation`() {

        // ARRANGE
        val errorCode = 500
        val category = null

        val expectedJsonError = JsonObject().apply {
            addProperty("status", "")
            addProperty("message", "")
        }

        val expectedResponse = Response.error<Joke>(
            errorCode,
            Gson().toJson(expectedJsonError).toResponseBody()
        )
        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        /* this is needed in order to clear the initial notification
        * that this observer receives with `isFetchingJoke` equals to `false`
        * that happens when the `JokeViewModel` is first created
        */
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        verify(viewStateObserverMock)
            .onChanged(
                JokeViewModel.ViewState(isFetchingJoke = false)
            )
    }

    @Test
    fun `Fetch random fact, when fetching fact from repository fails, then show fetch error message`() {

        // ARRANGE
        val errorCode = 500
        val errorMessage = "custom message"
        val category = null

        val expectedJsonError = JsonObject().apply {
            addProperty("status", "")
            addProperty("message", errorMessage)
        }

        val expectedResponse = Response.error<Joke>(
            errorCode,
            Gson().toJson(expectedJsonError).toResponseBody()
        )

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenReturn(CompletableDeferred(expectedResponse))

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowGenericErrorMessage
            )
    }

    @Test
    fun `Fetch random fact, when an exception is thrown from repository, then show fetch error message and hide loading animation`() {

        // ARRANGE
        val category = null

        whenever(jokeRepositoryMock.getRandomJokeAsync(category))
            .thenAnswer { throw Throwable() }
        clearInvocations(viewStateObserverMock)

        // ACT
        viewModel.getRandomJoke(category)

        // ASSERT
        verify(commandMock)
            .postValue(
                JokeViewModel.Command.ShowGenericErrorMessage
            )
        verify(viewStateObserverMock)
            .onChanged(
                JokeViewModel.ViewState(isFetchingJoke = false)
            )
    }
}
