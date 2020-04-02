package me.mairwunnx.covid19.api.store

import kotlinx.serialization.Serializable
import me.mairwunnx.covid19.api.CoronavirusInfectInitiator
import me.mairwunnx.covid19.api.CoronavirusInfectStatus

@Serializable
data class CoronavirusModel(
    var coronavirus: Coronavirus = Coronavirus(),
    var loggedInPlayers: MutableList<String> = mutableListOf(),
    var disinfectedInitially: MutableList<String> = mutableListOf(),
    var players: MutableList<Player> = mutableListOf()
) {
    @Serializable
    data class Coronavirus(
        var deaths: Long = 0,
        var epidemics: Int = 0,
        var finalized: Boolean = false,
        var infected: Long = 0
    )

    @Serializable
    data class Player(
        var hasImmunity: Boolean,
        var infectInitiator: CoronavirusInfectInitiator,
        var infectPercent: Double,
        var infectStatus: CoronavirusInfectStatus,
        var isDead: Boolean,
        var player: String,
        var stage: Int
    )
}
