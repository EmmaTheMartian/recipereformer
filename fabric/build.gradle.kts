plugins {
    id("com.github.johnrengelman.shadow")
}

fun prop(key: String): String = property(key) as String
fun rootProp(key: String): String = rootProject.property(key) as String

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("compileClasspath").extendsFrom(getByName("common"))
    getByName("runtimeClasspath").extendsFrom(getByName("common"))
    getByName("developmentFabric").extendsFrom(getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProp("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProp("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProp("fabric_kotlin_version")}")

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    "shadowBundle"(project(path = ":common", configuration = "transformProductionFabric"))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
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
