package me.mairwunnx.covid19.api.store

import kotlinx.serialization.Serializable
import me.mairwunnx.covid19.api.CoronavirusInfectInitiator
import me.mairwunnx.covid19.api.CoronavirusInfectStatus

@Serializable
data class CoronavirusModel(
    val coronavirus: Coronavirus = Coronavirus(),
    val players: MutableList<Player> = mutableListOf()
) {
    @Serializable
    data class Coronavirus(
        var deaths: Long = 0,
        var epidemic: Boolean = false,
        var lastEpidemicTime: Long = 0,
        var epidemics: Int = 0,
        var epidemicInfected: Long = 0,
        var finalized: Boolean = false,
        var infected: Long = 0
    )

    @Serializable
    data class Player(
        var player: String,
        var infectPercent: Double = 0.0,
        var infectStatus: CoronavirusInfectStatus = CoronavirusInfectStatus.None,
        var infectInitiator: CoronavirusInfectInitiator = CoronavirusInfectInitiator.None,
        var hasImmunity: Boolean = false,
        var infectStage: Int = 0,
        var isDead: Boolean = false,
        val meta: Meta = Meta()
    ) {
        @Serializable
        data class Meta(
            var loggedIn: Boolean = true,
            var initiallyInfected: Boolean = false,
            var initiallyDisinfected: Boolean = false,
            var killing: Boolean = false,
            var killingTicks: Int = 0
        )
    }
}
