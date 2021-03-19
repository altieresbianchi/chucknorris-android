package br.com.chucknorris.global.command

import br.com.chucknorris.global.events.SingleLiveEvent

interface CommandProvider {
    fun getCommand(): SingleLiveEvent<GenericCommand>
}
