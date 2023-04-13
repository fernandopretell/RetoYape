package com.fulbiopretell.retoyape

object Deps {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Libs.kotlinVersion}"
    const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Libs.coroutinesVersion}"
    const val kotlinCoroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.coroutinesVersion}"

    object BuildPlugins {
        const val androidGradle = "com.android.tools.build:gradle:${Versions.Libs.androidGradlePluginVersion}"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Libs.kotlinVersion}"
    }

    object Android {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.Libs.appCompat}"
        const val androidKTX = "androidx.core:core-ktx:${Versions.Libs.androidXVersion}"
        const val material = "com.google.android.material:material:${Versions.Libs.materialVersion}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.Libs.constraintLayoutVersion}"

        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.Libs.navigationVersion}"
        const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.Libs.navigationVersion}"

        const val multidex = "androidx.multidex:multidex:${Versions.Libs.multidexVersion}"

        const val work = "androidx.work:work-runtime-ktx:${Versions.Libs.workVersion}"
        const val legacySupportV4 = "androidx.legacy:legacy-support-v4:${Versions.Libs.legacySupportV4}"
        const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.Libs.recyclerView}"
    }

    object Libs {
        const val glide = "com.github.bumptech.glide:glide:${Versions.Libs.glideVersion}"
        const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.Libs.glideVersion}"
        const val glideOkHttp3 = "com.github.bumptech.glide:okhttp3-integration:${Versions.Libs.glideVersion}"

        const val flexbox = "com.google.android:flexbox:${Versions.Libs.flexboxVersion}"
        const val zxing = "com.google.zxing:core:${Versions.Libs.zxingVersion}"
        const val gson = "com.google.code.gson:gson:${Versions.Libs.gson}"
        const val dynamicanimation = "androidx.dynamicanimation:dynamicanimation-ktx:${Versions.Libs.dynamicanimation}"
    }

    object Test {
        const val junit = "junit:junit:${Versions.Test.junitVersion}"
        const val espressoCore = "com.android.support.test.espresso:espresso-core:${Versions.Test.espressoVersion}"
        const val runner = "com.android.support.test:runner:${Versions.Test.runnerVersion}"
        const val koinTest = "org.koin:koin-test:${Versions.Test.koinTestVersion}"
        const val roboelectric = "org.robolectric:robolectric:${Versions.Test.roboelectricVersion}"
        const val roboelectricMulidex = "org.robolectric:shadows-multidex:${Versions.Test.roboelectricVersion}"
        const val androidTestCore = "androidx.test:core:${Versions.Test.androidTestCoreVersion}"
        const val androidTestExt = "androidx.test.ext:junit:${Versions.Test.androidTestExtVersion}"
        const val androidTestRunner = "androidx.test:runner:${Versions.Test.androidTestCoreVersion}"
        const val androidTestRules = "androidx.test:rules:${Versions.Test.androidTestCoreVersion}"
        const val kluent = "org.amshove.kluent:kluent-android:${Versions.Test.kluentVersion}"
        const val mockk = "io.mockk:mockk:${Versions.Test.mockkVersion}"
        const val mockkAndroid = "io.mockk:mockk-android:${Versions.Test.mockkVersion}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Test.coroutines}"
    }
}
