package br.com.chucknorris.repository.di

import br.com.chucknorris.BuildConfig
import br.com.chucknorris.repository.JokeRepository
import br.com.chucknorris.repository.api.JokeApiClient
import br.com.chucknorris.repository.api.RestClient
import org.koin.core.module.Module
import org.koin.dsl.module

val jokeRepositoryModule: Module = module {
    factory {
        JokeRepository(jokeApiClient = get())
    }

    single {
        RestClient.getApiClient(
            context = get(),
            serviceClass = JokeApiClient::class.java,
            baseEndpoint = BuildConfig.BASE_API
        )
    }
}
