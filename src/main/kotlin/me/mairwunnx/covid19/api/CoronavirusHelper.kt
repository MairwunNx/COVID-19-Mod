package me.mairwunnx.covid19.api

import net.minecraft.util.math.AxisAlignedBB
import java.util.concurrent.ThreadLocalRandom

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
