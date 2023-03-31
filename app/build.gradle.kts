@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.parcelize)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/detekt.yml")
    autoCorrect = true
}

android {
    namespace = "edu.uaux.pheart"
    compileSdk = 33
    defaultConfig {
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
        setProperty("archivesBaseName", "Digital_Health_v$versionName")
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    buildTypes {
        getByName("release") {
            isDebuggable = false
            lint {
                disable.add("MissingTranslation")
                disable.add("ExtraTranslation")
            }
        }
        getByName("debug") {
            isDebuggable = true
            versionNameSuffix = "-debug"
            aaptOptions.cruncherEnabled = false // Disable png crunching
        }
    }
    lint {
        abortOnError = false
        sarifReport = true
    }
    packagingOptions {
        with(resources.excludes) {
            // Remove .kotlin_module files that'd only be necessary for Kotlin reflection
            add("META-INF/*.kotlin_module")

            // Remove unnecessary .version and .properties files
            add("META-INF/*.version")
            add("/*.properties")

            // Remove kotlinx.coroutines debug infrastructure
            add("DebugProbesKt.bin")
        }
    }
}

kotlin {
    kotlinDaemonJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    )
}

dependencies {
    // Local libraries
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin <3
    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.properties)

    // Core
    implementation(libs.koin.android)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.appcompat)

    // UI
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment)
    implementation(libs.mpandroidchart)
    implementation(libs.modernandroidpreferences)

    // Camera & face detection
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.mlkit)
    implementation(libs.playservices.mlkit)
    implementation(libs.jtransforms)

    // Lifecycle & ViewModel
    implementation(libs.bundles.androidx.lifecycle)

    // Room
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    // Health
    implementation(libs.timber)
    debugImplementation(libs.leakcanary)

    // Testing
    testImplementation(libs.bundles.kotest)

    // Formatting rules for detekt
    detektPlugins(libs.detekt.formatting)
}

tasks {
    withType<Detekt> {
        jvmTarget = JavaVersion.VERSION_1_8.toString()

        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
}