package me.mairwunnx.covid19.api.store

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileNotFoundException

@OptIn(kotlinx.serialization.UnstableDefault::class)
object CoronavirusStore {
    private val logger = LogManager.getLogger()
    private var data = CoronavirusModel()
    private lateinit var path: String
    private val jsonInstance = Json(
        JsonConfiguration(
            encodeDefaults = true,
            ignoreUnknownKeys = true,
            isLenient = false,
            serializeSpecialFloatingPointValues = false,
            allowStructuredMapKeys = true,
            prettyPrint = true, // todo: before release change this value to false.
            unquotedPrint = false,
            useArrayPolymorphism = false
        )
    )

    fun init(dist: String) {
        path = "${dist}${File.separator}coronavirus.json"
        load()
    }

    fun load() {
        try {
            val json = File(path).readText()
            data = jsonInstance.parse(CoronavirusModel.serializer(), json)
        } catch (ex: FileNotFoundException) {
            logger.error("Configuration file ($path) not found!")
            logger.warn("The default configuration will be used")
        }
    }

    fun take() = data

    fun save() {
        File(path).parentFile.mkdirs()

        logger.info("Saving configuration `COVID-19`")
        val raw = jsonInstance.stringify(CoronavirusModel.serializer(), data)
        try {
            File(path).writeText(raw)
        } catch (ex: SecurityException) {
            logger.error(
                "An error occurred while saving commands configuration", ex
            )
        }
    }
}
