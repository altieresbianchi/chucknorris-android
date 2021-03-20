package br.com.chucknorris.feature.joke.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.chucknorris.global.command.CommandProvider
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.repository.JokeRepository
import br.com.chucknorris.repository.model.Joke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class JokeListViewModel(
    private val jokeRepository: JokeRepository,
    private val coroutineContext: CoroutineContext,
    private val commandProvider: CommandProvider
) : ViewModel() {

    val command: MutableLiveData<GenericCommand> = commandProvider.getCommand()
    private val coroutineScope = CoroutineScope(coroutineContext)
    val viewState: MutableLiveData<ViewState> = MutableLiveData()

    data class ViewState(
        val isFetchingJoke: Boolean = false
    )

    sealed class Command : GenericCommand() {
        object ShowEmptyList : Command()
        object ShowGenericErrorMessage : Command()
        data class ShowJokeList(val jokeList: List<Joke>) : Command()
    }

    init {
        viewState.value = ViewState()
    }

    private fun currentViewState(): ViewState = viewState.value!!

    fun fetchJokesBySearch(searchText: String) {
        coroutineScope.launch(coroutineContext) {
            viewState.postValue(currentViewState().copy(isFetchingJoke = true))
            try {
                val response = jokeRepository.fetchJokesBySearchAsync(searchText).await()
                viewState.postValue(currentViewState().copy(isFetchingJoke = false))

                if (response.isSuccessful) {
                    var jokeList = emptyList<Joke>()

                    response.body()?.let { jokeResponse ->
                        jokeList = jokeResponse.result
                    }

                    if (jokeList.isNotEmpty())
                        command.postValue(Command.ShowJokeList(jokeList))
                    else
                        command.postValue(Command.ShowEmptyList)

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