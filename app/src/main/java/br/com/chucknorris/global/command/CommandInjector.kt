package br.com.phaneronsoft.stockcontrol.global.command

import br.com.chucknorris.global.command.CommandProvider
import br.com.chucknorris.global.command.GenericCommand
import br.com.chucknorris.global.events.SingleLiveEvent

object CommandInjector : CommandProvider {
    override fun getCommand(): SingleLiveEvent<GenericCommand> = SingleLiveEvent()
}
