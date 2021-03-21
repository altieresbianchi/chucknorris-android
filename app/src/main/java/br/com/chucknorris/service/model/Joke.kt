package br.com.chucknorris.service.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Joke(
    val id: String,
    val value: String,
    @SerializedName("icon_url")
    val iconUrl: String,
) : Serializable
