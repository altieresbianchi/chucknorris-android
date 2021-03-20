package br.com.chucknorris.repository.model

import java.io.Serializable

data class JokeResponse(
    val total: Int,
    val result: List<Joke>,
) : Serializable
