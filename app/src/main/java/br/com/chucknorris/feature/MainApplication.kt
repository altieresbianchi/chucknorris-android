package br.com.chucknorris.feature

import android.app.Application
import br.com.chucknorris.di.commandInjectorModule
import br.com.chucknorris.di.coroutineModule
import br.com.chucknorris.repository.di.jokeRepositoryModule
import br.com.phaneronsoft.stockcontrol.feature.product.di.jokeViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        this.initializeKoin();
    }

    private fun initializeKoin() {

        startKoin {
            androidContext(this@MainApplication)

            // Use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            modules(
                listOf(
                    coroutineModule,
                    commandInjectorModule,
                    jokeRepositoryModule,
                    jokeViewModelModule,
                )
            )
        }
    }
}