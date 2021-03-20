package br.com.chucknorris.feature.joke.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.chucknorris.global.command.CommandProvider
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.service.contract.JokeRepository
import br.com.chucknorris.service.model.Joke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class JokeViewModel(
    private val jokeRepository: JokeRepository,
    private val coroutineContext: CoroutineContext,
    private val commandProvider: CommandProvider
) : ViewModel() {

    val command: MutableLiveData<GenericCommand> = commandProvider.getCommand()
    val commandCategory: MutableLiveData<GenericCommand> = commandProvider.getCommand()
    private val coroutineScope = CoroutineScope(coroutineContext)
    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    data class ViewState(
        val isFetchingCategories: Boolean = false,
        val isFetchingJoke: Boolean = false
    )

    sealed class Command : GenericCommand() {
        object ShowGenericErrorMessage : Command()
        data class ShowJokeCard(val joke: Joke) : Command()
        data class ShowCategoryList(val categoryList: List<String>) : Command()
    }

    init {
        viewState.value = ViewState()
    }

    private fun currentViewState(): ViewState = viewState.value!!

    fun fetchCategories() {
        coroutineScope.launch(coroutineContext) {
            viewState.postValue(currentViewState().copy(isFetchingCategories = true))
            try {
                val responseCategories = jokeRepository.fetchCategoriesAsync().await()
                viewState.postValue(currentViewState().copy(isFetchingCategories = false))

                if (responseCategories.isSuccessful) {
                    responseCategories.body()?.let { categoryList ->
                        commandCategory.postValue(Command.ShowCategoryList(categoryList))
                    }
                }

            } catch (t: Throwable) {
                Timber.e(t)
                viewState.postValue(currentViewState().copy(isFetchingCategories = false))
            }
        }
    }

    fun getRandomJoke(category: String?) {
        coroutineScope.launch(coroutineContext) {
            viewState.postValue(currentViewState().copy(isFetchingJoke = true))
            try {
                val response = jokeRepository.getRandomJokeAsync(category).await()
                viewState.postValue(currentViewState().copy(isFetchingJoke = false))

                if (response.isSuccessful) {
                    response.body()?.let { joke ->
                        command.postValue(Command.ShowJokeCard(joke))
                    }
                } else {
                    command.postValue(Command.ShowGenericErrorMessage)
                }

            } catch (t: Throwable) {
                Timber.e(t)
                command.postValue(Command.ShowGenericErrorMessage)
                viewState.postValue(currentViewState().copy(isFetchingJoke = false))
            }
        }
    }
}