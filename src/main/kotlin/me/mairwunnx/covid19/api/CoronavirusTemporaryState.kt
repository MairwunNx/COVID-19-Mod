package me.mairwunnx.covid19.api

import kotlin.properties.Delegates

object CoronavirusTemporaryState {
    var epidemic: Boolean = false
    val stateMap: HashMap<String, MutableSet<Double>> by Delegates.observable(
        hashMapOf(), { _, _, new ->
            new.keys.forEach {
                if (new[it]?.count()!! >= 2) {
                    new[it]?.remove(new[it]?.first())
                }
            }
        }
    )
}
