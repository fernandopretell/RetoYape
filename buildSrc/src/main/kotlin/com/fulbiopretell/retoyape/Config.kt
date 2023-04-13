package com.fulbiopretell.retoyape

object Config {
    const val packageName = "com.fulbiopretell.retoyape"
    const val packageNameCore = "com.fulbiopretell.retoyape.core"
    const val packageNameBaseUi = "com.fulbiopretell.retoyape.base_ui"
    const val name = "RetoYape"
    val isCiServer = System.getenv().containsKey("CI")

    object Signing {
        const val env = "config/key.properties"
        const val storeFile = "store.file"
        const val storePassword = "store.password"
        const val keyAlias = "key.alias"
        const val keyPassword = "key.password"
    }

    object Packaging {
        val excludes = arrayOf(
            "LICENSE.txt",
            "META-INF/DEPENDENCIES",
            "META-INF/ASL2.0",
            "META-INF/NOTICE",
            "META-INF/LICENSE",
            "META-INF/core_release.kotlin_module"
        )
    }
}
