fun rootProp(key: String): String = rootProject.property(key) as String

architectury {
    common(rootProp("enabled_platforms").split(","))
}

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation("net.fabricmc:fabric-loader:${rootProp("fabric_loader_version")}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProp("kotlin_version")}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${rootProp("kotlin_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProp("kotlin_coroutines_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${rootProp("kotlin_serialization_version")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProp("kotlin_serialization_version")}")
}
