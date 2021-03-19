package br.com.chucknorris.di

import br.com.phaneronsoft.stockcontrol.global.command.CommandInjector
import br.com.chucknorris.global.command.CommandProvider
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val coroutineModule: Module = module {
    factory<CoroutineContext> { Dispatchers.IO }
}

val commandInjectorModule: Module = module {
    single<CommandProvider> { CommandInjector }
}