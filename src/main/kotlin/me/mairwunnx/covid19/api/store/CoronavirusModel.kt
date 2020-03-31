package me.mairwunnx.covid19.api.store

import kotlinx.serialization.Serializable
import me.mairwunnx.covid19.api.CoronavirusInfectInitiator
import me.mairwunnx.covid19.api.CoronavirusInfectStatus

@Serializable
data class CoronavirusModel(
    var coronavirus: Coronavirus = Coronavirus(),
    var players: MutableList<Player> = mutableListOf()
) {
    @Serializable
    data class Coronavirus(
        var deaths: Int = 0,
        var epidemics: Int = 0,
        var finalized: Boolean = false,
        var infected: Int = 0
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
