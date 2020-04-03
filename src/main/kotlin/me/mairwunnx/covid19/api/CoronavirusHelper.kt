package me.mairwunnx.covid19.api

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

/**
 * Executes action with chance (between 0 and 1).
 */
inline fun withChance(chance: Double, action: () -> Unit) {
    if (Math.random() < chance) action.invoke()
}

/**
 * @param baseValue value to decrement or increment.
 * @param modifier modifier (value to increment or decrement).
 * @return double number with randomly increment or decrement.
 */
fun rndDoubleDecrementOrIncrement(
    baseValue: Double, modifier: Double
): Double {
    val newModifier = randomBetweenZeroAnd(modifier)
    val result = ThreadLocalRandom.current().nextInt(0, 2)
    return if (result == 1) baseValue - newModifier else baseValue + newModifier
}

/**
 * @param baseValue value to decrement or increment.
 * @param modifier modifier (value to increment or decrement).
 * @return long number with randomly increment or decrement.
 */
fun rndLongDecrementOrIncrement(
    baseValue: Long, modifier: Long
): Long {
    val newModifier = randomBetweenZeroAnd(modifier)
    val result = ThreadLocalRandom.current().nextInt(0, 2)
    return if (result == 1) baseValue - newModifier else baseValue + newModifier
}

/**
 * @param other max long value.
 * @return random long number between zero and [other] value.
 */
fun randomBetweenZeroAnd(
    other: Long
) = ThreadLocalRandom.current().nextLong(0, other)

/**
 * @param other max double value.
 * @return random double number between zero and [other] value.
 */
fun randomBetweenZeroAnd(
    other: Double
) = ThreadLocalRandom.current().nextDouble(0.0, other)

/**
 * @return true if number is event.
 */
fun isEvenNumber(number: Int) = (number and 1) == 0

/**
 * @return expanded [AxisAlignedBB] instance.
 */
fun expandAxisAlignedBB(
    size: Double,
    axisAlignedBB: AxisAlignedBB
): AxisAlignedBB = axisAlignedBB.expand(size, size, size).expand(-size, -size, -size)

fun playSound(
    player: PlayerEntity,
    soundEvent: SoundEvent,
    soundCategory: SoundCategory,
    volume: Float = 0.8f
) {
    DistExecutor.runWhenOn(Dist.CLIENT) {
        Runnable {
            Minecraft.getInstance().world.playSound(
                player.position.x.toDouble(),
                player.position.y + player.eyeHeight.toDouble(),
                player.position.z.toDouble(),
                soundEvent,
                soundCategory,
                volume,
                1.0f,
                false
            )
            player.entity.playSound(soundEvent, volume, 1.0f)
        }
    }

    DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
        Runnable {
            player.world.playSound(
                null,
                player.position.x.toDouble(),
                player.position.y + player.eyeHeight.toDouble(),
                player.position.z.toDouble(),
                soundEvent,
                soundCategory,
                volume,
                1.0f
            )
        }
    }
}

/**
 * Updates virus state for specified player.
 * @param name target player name.
 */
fun updatePlayerVirusState(name: String) {
    if (CoronavirusTemporaryState.virusStateHashMap[name] != null) {
        CoronavirusTemporaryState.virusStateHashMap[name]?.add(CoronavirusAPI.getInfectPercent(name))
    } else {
        CoronavirusTemporaryState.virusStateHashMap[name] = hashSetOf()
    }

    val equalsDelta = 0.0001
    val values = CoronavirusTemporaryState.virusStateHashMap[name]!!

    CoronavirusAPI.getPlayer(name)?.infectStatus = when {
        abs(values.last() - values.elementAt(values.count() - 2)) < equalsDelta -> CoronavirusInfectStatus.Suspended
        values.last() > values.elementAt(values.count() - 2) -> CoronavirusInfectStatus.Actively
        values.last() < values.elementAt(values.count() - 2) -> CoronavirusInfectStatus.Recession
        else -> CoronavirusInfectStatus.Suspended
    }
}
