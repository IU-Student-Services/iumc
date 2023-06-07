package com.github.jsh32.iumc.proxy.utils

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.loader.ConfigurationLoader
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.io.File


/**
 * Annotate all loaded config classes with this.
 */
annotation class Config(val file: String)

/**
 * Result of loading file.
 */
data class ConfigLoadResult<T>(val config: T, val created: Boolean)

/**
 * Load/create a config from a file.
 */
fun <T> loadConfig(config: Class<T>, file: File, required: Boolean = false, serializerConfig: ((TypeSerializerCollection.Builder) -> Unit)? = null): ConfigLoadResult<T> {
    val loader: ConfigurationLoader<CommentedConfigurationNode> =
        HoconConfigurationLoader.builder()
            .defaultOptions { opts ->
                opts.shouldCopyDefaults(true)

                if (serializerConfig != null) {
                    opts.serializers(serializerConfig)
                } else {
                    opts
                }
            }
            .prettyPrinting(true)
            .path(file.toPath())
            .build()

    val node = loader.load()

    val loaded = node.get(config)!!

    if (!file.exists()) {
        // Set node to default value of config class and save it.
        node.set(config, loaded)
        loader.save(node)

        if (required) {
            throw IllegalStateException("Config (${file.toPath()}) was created and needs to be filled out.")
        }
    }

    return ConfigLoadResult(loaded, !file.exists())
}

/**
 * Load/create a config from a file.
 */
inline fun <reified T> loadConfig(file: File, required: Boolean = false) = loadConfig(T::class.java, file, required)