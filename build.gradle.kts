import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    kotlin("jvm") version "2.1.0" apply false
}

fun prop(key: String): String = property(key) as String
fun rootProp(key: String): String = rootProject.property(key) as String

architectury {
    minecraft = prop("minecraft_version")
}

allprojects {
    group = rootProp("maven_group")
    version = rootProp("mod_version")
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    base {
        // Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
        archivesName = "${rootProp("archives_name")}-${project.name}"
    }

    repositories {
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
    }

    dependencies {
        "minecraft"("net.minecraft:minecraft:${rootProp("minecraft_version")}")

        val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
        @Suppress("UnstableApiUsage")
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${rootProp("parchment_version")}@zip")
        })

        implementation("org.jetbrains.kotlin:kotlin-scripting-common:${rootProp("kotlin_scripting_version")}")
        implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:${rootProp("kotlin_scripting_version")}")
        implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:${rootProp("kotlin_scripting_version")}")
        implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:${rootProp("kotlin_scripting_version")}")
        implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:${rootProp("kotlin_scripting_version")}")
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = 21
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
    }

    // Configure Maven publishing.
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }

        repositories {
        }
    }
}
