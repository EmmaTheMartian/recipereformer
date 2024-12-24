plugins {
    id("com.github.johnrengelman.shadow")
}

fun prop(key: String): String = property(key) as String
fun rootProp(key: String): String = rootProject.property(key) as String

architectury {
    platformSetupLoomIde()
    neoForge()
}

@Suppress("UnstableApiUsage")
configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("compileClasspath").extendsFrom(getByName("common"))
    getByName("runtimeClasspath").extendsFrom(getByName("common"))
    getByName("developmentNeoForge").extendsFrom(getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }

    create("library") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("implementation").extendsFrom(getByName("library"))
    getByName("forgeRuntimeLibrary").extendsFrom(getByName("library"))
}

repositories {
    maven("https://maven.neoforged.net/releases") {
        name = "NeoForged"
    }
    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        name = "Kotlin for Forge"
    }
}

dependencies {
    neoForge("net.neoforged:neoforge:${rootProp("neoforge_version")}")

    implementation("thedarkcolour:kotlinforforge-neoforge:${rootProp("kff_version")}:slim") {
        exclude("net.neoforged.fancymodloader", "loader")
    }

    "library"("org.jetbrains.kotlin:kotlin-stdlib:${rootProp("kotlin_version")}")
    "library"("org.jetbrains.kotlin:kotlin-reflect:${rootProp("kotlin_version")}")
    "library"("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProp("kotlin_coroutines_version")}")
    "library"("org.jetbrains.kotlinx:kotlinx-serialization-core:${rootProp("kotlin_serialization_version")}")
    "library"("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProp("kotlin_serialization_version")}")

    "forgeRuntimeLibrary"("org.jetbrains.kotlin:kotlin-scripting-common:${rootProp("kotlin_scripting_version")}")
    "forgeRuntimeLibrary"("org.jetbrains.kotlin:kotlin-scripting-jvm:${rootProp("kotlin_scripting_version")}")
    "forgeRuntimeLibrary"("org.jetbrains.kotlin:kotlin-scripting-jvm-host:${rootProp("kotlin_scripting_version")}")
    "forgeRuntimeLibrary"("org.jetbrains.kotlin:kotlin-scripting-dependencies:${rootProp("kotlin_scripting_version")}")
    "forgeRuntimeLibrary"("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:${rootProp("kotlin_scripting_version")}")

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    "shadowBundle"(project(path = ":common", configuration = "transformProductionNeoForge"))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(mapOf(
            "version" to project.version
        ))
    }
}

tasks.shadowJar {
    configurations = listOf(project.configurations["shadowBundle"])
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    val shadowJar = tasks.shadowJar.get()
    input.set(shadowJar.archiveFile)
}
