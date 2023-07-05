package com.github.jsh32.iumc.proxy

import java.util.*

object Globals {
    val VERSION_HASH: String = run {
        val gitProps = Properties()
        IUMCProxy::class.java.classLoader.getResourceAsStream("git.properties")
            .use { resourceAsStream -> gitProps.load(resourceAsStream) }
        gitProps.getProperty("git.hash")
    }
}