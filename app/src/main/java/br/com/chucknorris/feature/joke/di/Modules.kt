package br.com.phaneronsoft.stockcontrol.feature.product.di

import br.com.chucknorris.feature.joke.viewmodel.JokeListViewModel
import br.com.chucknorris.feature.joke.viewmodel.JokeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val jokeViewModelModule: Module = module {
    viewModel {
        JokeViewModel(
            jokeRepository = get(),
            commandProvider = get(),
            coroutineContext = get()
        )
    }
}
val jokeListViewModelModule: Module = module {
    viewModel {
        JokeListViewModel(
            jokeRepository = get(),
            commandProvider = get(),
            coroutineContext = get()
        )
    }
}