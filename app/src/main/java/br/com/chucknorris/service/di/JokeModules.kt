package br.com.chucknorris.service.di

import br.com.chucknorris.BuildConfig
import br.com.chucknorris.service.repository.JokeApiRepository
import br.com.chucknorris.service.api.JokeApiClient
import br.com.chucknorris.service.api.RestClient
import br.com.chucknorris.service.contract.JokeRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val jokeRepositoryModule: Module = module {
    factory<JokeRepository> {
        JokeApiRepository(jokeApiClient = get())
    }

    single {
        RestClient.getApiClient(
            context = get(),
            serviceClass = JokeApiClient::class.java,
            baseEndpoint = BuildConfig.BASE_API
        )
    }
}
